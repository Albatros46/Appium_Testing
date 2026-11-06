package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.servet.appium.base.BaseSettingsTest;
import com.servet.appium.support.Adb;

import io.appium.java_client.AppiumBy;

/** The Android Settings app: on every device, nothing to install. */
public class SettingsAppTest extends BaseSettingsTest {

    @Test
    public void the_settings_app_opens_on_its_home_screen() {
        assertThat(driver.getCurrentPackage()).isEqualTo(SETTINGS);
        assertThat(visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Search settings\")")).isDisplayed())
                .isTrue();
    }

    /** The page shows what adb reports: two independent ways to read the same fact. */
    @Test
    public void the_about_page_shows_the_model_that_adb_reports() {
        String model = Adb.shell("getprop", "ro.product.model");

        driver.findElement(AppiumBy.androidUIAutomator(scrollTo("About emulated device"))).click();

        assertThat(visible(AppiumBy.androidUIAutomator(scrollTo(model))).isDisplayed()).isTrue();
    }

    @Test
    public void the_back_key_returns_to_the_home_screen() {
        driver.findElement(AppiumBy.androidUIAutomator(scrollTo("About emulated device"))).click();
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Device name\")"));

        driver.navigate().back();

        assertThat(visible(AppiumBy.androidUIAutomator(scrollTo("About emulated device"))).isDisplayed()).isTrue();
    }

    /** Lets Android scroll the list until the text is on the screen: no coordinates, any screen size. */
    private static String scrollTo(String text) {
        return "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"" + text
                + "\"))";
    }
}
