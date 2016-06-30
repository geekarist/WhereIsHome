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
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.annimon.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
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
        assertThat(name, notNullValue());
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

        assertObjectAbsence("com.github.geekarist.whereishome:id/place_remove");
    }

    @Test
    public void shouldShowWeeklyCommutes() {

        // GIVEN I am on the places screen
        assertObjectPresence("com.github.geekarist.whereishome:id/commuting_time_button_add_place");
        // And all places have been removed
        assertObjectAbsence("com.github.geekarist.whereishome:id/place_container");
        // And internet is reachable
        assertThat(isNetworkConnected(), is(true));

        // WHEN I pick a place
        pickPlace("98 rue brillat savarin, paris");

        // THEN the list of places has 1 item
        List<UiObject2> foundAddresses = findObjects("com.github.geekarist.whereishome:id/place_text_address");
        assertThat(foundAddresses.size(), is(1));
        // And it mentions the address I have picked
        assertThat(foundAddresses.get(0).getText(), containsString("98 Rue Brillat-Savarin, 75013 Paris-13E-Arrondissement, France"));
        List<UiObject2> foundTimes = findObjects("com.github.geekarist.whereishome:id/place_text_commute_time");
        // Indicating 'this is your home'
        assertThat(foundTimes.size(), is(1));
        assertThat(foundTimes.get(0).getText(), containsString("This is your home"));

        // WHEN I pick another place
        // Click com.github.geekarist.whereishome:id/commuting_time_button_add_place
        pickPlace("2 place carpeaux, puteaux");

        // THEN the list of places has 2 items
        // And it mentions the new address
        // Indicating an ETA, the number of times chosen and the total week time
        // And the screen indicates the total week time
        foundAddresses = findObjects("com.github.geekarist.whereishome:id/place_text_address");
        assertThat(foundAddresses.size(), is(2));
        // And it mentions the address I have picked
        assertThat(foundAddresses.get(1).getText(), containsString("2 Place Carpeaux, 92800 Puteaux, France"));
        foundTimes = findObjects("com.github.geekarist.whereishome:id/place_text_commute_time");
        // Indicating 'this is your home'
        assertThat(foundTimes.size(), is(2));
        assertThat("'" + foundTimes.get(1).getText() + "' should match /\\d+ minutes/", foundTimes.get(1).getText().matches("\\d+ minutes"), is(true));

        // WHEN I pick another place
        pickPlace("Antrebloc");

        // THEN the list of places has 3 items
        foundAddresses = findObjects("com.github.geekarist.whereishome:id/place_text_address");
        assertThat(foundAddresses.size(), is(3));
        // And it mentions the new address
        assertThat(foundAddresses.get(2).getText(), containsString("5 rue Henri Barbusse, XXXXX Villejuif, France"));
        // Indicating an ETA, the number of times chosen and the total week time
        // And the screen indicates the total week time for all items
    }

    private void pickPlace(String placeToPick) {
        // Click com.github.geekarist.whereishome:id/commuting_time_button_add_place
        click("com.github.geekarist.whereishome:id/commuting_time_button_add_place");
        // Click com.google.android.gms:id/places_ui_menu_main_search
        click("com.google.android.gms:id/places_ui_menu_main_search");
        // Write "2 place carpeaux, puteaux"
        setText("com.google.android.gms:id/input", placeToPick);
        // Click the first com.google.android.gms:id/place_autocomplete_prediction_primary_text in com.google.android.gms:id/list
        List<UiObject2> suggestionsForHome = findObjects("com.google.android.gms:id/place_autocomplete_prediction_primary_text");
        suggestionsForHome.get(0).click();
        // Click com.google.android.gms:id/title
        click("com.google.android.gms:id/title");
        // Click com.github.geekarist.whereishome:id/pick_commute_button_accept
        click("com.github.geekarist.whereishome:id/pick_commute_button_accept");
    }

    private void assertObjectAbsence(String resourceName) {
        assertThat(mDevice.hasObject(By.res(resourceName)), is(false));
    }

    private void assertObjectPresence(String resourceName) {
        assertThat(mDevice.hasObject(By.res(resourceName)), is(true));
    }

    private List<UiObject2> findObjects(String resourceName) {
        return mDevice.wait(Until.findObjects(By.res(resourceName)), FIND_OBJ_TIMEOUT);
    }

    private void setText(String resourceName, String text) {
        mDevice.wait(Until.findObject(By.res(resourceName)), FIND_OBJ_TIMEOUT).setText(text);
    }

    private void click(String resourceName) {
        mDevice.wait(Until.findObject(By.res(resourceName)), FIND_OBJ_TIMEOUT).click();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isNetworkConnected() {
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    // TODO make this work
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