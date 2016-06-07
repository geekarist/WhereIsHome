package com.github.geekarist.whereishome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private static final long LAUNCH_TIMEOUT = 15_000;
    private static final String TARGET_PACKAGE = InstrumentationRegistry.getTargetContext().getPackageName();
    private static final long FIND_OBJ_TIMEOUT = 2_000;
    private UiDevice mDevice;

    @Before
    public void launchTargetActivity() {

        // Start launcher
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();
        String name = getLauncherPackageName();
        assertThat(name, Matchers.notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(name).depth(0)), LAUNCH_TIMEOUT);

        // Launch target activity
        Context context = InstrumentationRegistry.getContext();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(launchIntent);
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void shouldRemoveAllPlaces() {

        while (mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/place_remove"))) {
            List<UiObject2> removeButtons = mDevice.findObjects(By.res("com.github.geekarist.whereishome:id/place_remove"));
            Stream.of(removeButtons).findFirst().ifPresent((button) -> {
                button.click();
                mDevice.wait(Until.findObject(By.text("OK")), FIND_OBJ_TIMEOUT).click();
            });
        }

        assertThat(mDevice.hasObject(By.res("com.github.geekarist.whereishome:id/place_remove")), is(false));
    }

    private String getLauncherPackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager packageManager = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}