package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.servet.appium.base.BaseAppiumTest;

import io.appium.java_client.AppiumBy;

/** The Android Settings app: on every device, nothing to install. */
public class SettingsAppTest extends BaseAppiumTest {

    @Test
    public void the_settings_app_opens_on_its_home_screen() {
        assertThat(driver.getCurrentPackage()).isEqualTo(SETTINGS);
        assertThat(visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Search settings\")")).isDisplayed())
                .isTrue();
    }
}
