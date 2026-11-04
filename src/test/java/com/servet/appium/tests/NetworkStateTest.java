package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseAppiumTest;
import com.servet.appium.support.Adb;

import io.appium.java_client.AppiumBy;

/** Changes the device state from outside with adb and checks that the app shows it. */
public class NetworkStateTest extends BaseAppiumTest {

    private static final By AIRPLANE_SWITCH = AppiumBy.id("com.android.settings:id/switchWidget");
    /** The switch itself is not clickable, the row around it is: tap the title like a user does. */
    private static final By AIRPLANE_ROW = AppiumBy.androidUIAutomator("new UiSelector().text(\"Airplane mode\")");

    /** Runs even when the test fails: an emulator left offline would break every following test. */
    @AfterMethod(alwaysRun = true)
    public void airplaneModeOff() {
        Adb.shell("cmd", "connectivity", "airplane-mode", "disable");
    }

    @Test
    public void airplane_mode_set_by_adb_is_shown_in_the_settings() {
        Adb.shell("cmd", "connectivity", "airplane-mode", "enable");

        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\"Network & internet\"))")).click();

        assertThat(visible(AIRPLANE_SWITCH).getAttribute("checked")).isEqualTo("true");
    }

    @Test
    public void airplane_mode_switched_in_the_settings_is_seen_by_adb() {
        assertThat(Adb.shell("cmd", "connectivity", "airplane-mode")).isEqualTo("disabled");
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\"Network & internet\"))")).click();

        visible(AIRPLANE_ROW).click();

        wait.until(d -> "enabled".equals(Adb.shell("cmd", "connectivity", "airplane-mode")));
    }
}
