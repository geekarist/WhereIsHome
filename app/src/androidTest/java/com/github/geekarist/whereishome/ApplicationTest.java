package com.github.geekarist.whereishome;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.annimon.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private static final long LAUNCH_TIMEOUT = 15_000;
    private static final String TARGET_PACKAGE = InstrumentationRegistry.getTargetContext().getPackageName();
    private static final long FIND_OBJ_TIMEOUT = 2_000;
    private UiDevice mDevice;
    private Context mContext;
    private ConnectivityManager mConnectivityManager;

    @Before
    public void setUp() {

        // Start launcher
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();
        String name = getLauncherPackageName();
        assertThat(name, Matchers.notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(name).depth(0)), LAUNCH_TIMEOUT);

        // Launch target activity
        mContext = InstrumentationRegistry.getContext();
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(launchIntent);
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        removeAllPlaces();
    }

    private void removeAllPlaces() {

        int iterationsLeft = 100;
        while (mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/place_remove"))) {
            List<UiObject2> removeButtons = mDevice.findObjects(By.res("com.github.geekarist.whereishome:id/place_remove"));
            Stream.of(removeButtons).findFirst().ifPresent((button) -> {
                button.click();
                mDevice.wait(Until.findObject(By.text("OK")), FIND_OBJ_TIMEOUT).click();
                mDevice.waitForWindowUpdate(null, 5_000);
            });
            if (iterationsLeft-- < 0) {
                break;
            }
        }

        assertThat(mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/place_remove")), is(false));
    }

    @Test
    public void shouldShowWeeklyCommutes() {

        // Given I am on the places screen
        assertThat(mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/commuting_time_button_add_place")), is(true));
        // And all places have been removed
        assertThat(mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/place_container")), is(false));
        // And internet is reachable
        assertThat(isNetworkConnected(), is(true));
        assertThat(isDistanceCalculationApiAccessible(), is(true));

        // When I pick a place

        // Then the list of places has 1 item
        // And it mentions the address I have picked
        // Indicating 'this is your home'

        // When I pick another place

        // Then the list of places has 2 items
        // And it mentions the new address
        // Indicating an ETA, the number of times chosen and the total week time
        // And the screen indicates the total week time

        // When I pick another place

        // Then the list of places has 3 items
        // And it mentions the new address
        // Indicating an ETA, the number of times chosen and the total week time
        // And the screen indicates the total week time for all items
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isNetworkConnected() {
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private boolean isDistanceCalculationApiAccessible() {
        try {
            URL url = new URL("https://developer.citymapper.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getLauncherPackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager packageManager = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}