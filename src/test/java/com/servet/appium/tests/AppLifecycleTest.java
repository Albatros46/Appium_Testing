package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseSettingsTest;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.appmanagement.ApplicationState;

/** Mobile-only test types: what happens to the app when the device or the user interrupts it. */
public class AppLifecycleTest extends BaseSettingsTest {

    private static final String SEARCH =
            "new UiSelector().text(\"Search settings\")";

    @Test
    public void the_app_survives_a_rotation_and_back() {
        rotateTo(ScreenOrientation.LANDSCAPE);
        try {
            assertThat(driver.getOrientation()).isEqualTo(ScreenOrientation.LANDSCAPE);
            assertThat(driver.getCurrentPackage()).isEqualTo(SETTINGS);
        } finally {
            rotateTo(ScreenOrientation.PORTRAIT); // the device is shared: leave it as it was
        }
        assertThat(visible(AppiumBy.androidUIAutomator(SEARCH)).isDisplayed()).isTrue();
    }

    @Test
    public void the_app_returns_from_the_background() {
        driver.runAppInBackground(Duration.ofSeconds(2));

        wait.until(d -> driver.queryAppState(SETTINGS) == ApplicationState.RUNNING_IN_FOREGROUND);
        assertThat(visible(AppiumBy.androidUIAutomator(SEARCH)).isDisplayed()).isTrue();
    }

    @Test
    public void a_killed_app_can_be_started_again() {
        assertThat(driver.terminateApp(SETTINGS)).isTrue();
        assertThat(driver.queryAppState(SETTINGS)).isEqualTo(ApplicationState.NOT_RUNNING);

        driver.activateApp(SETTINGS);

        wait.until(d -> driver.queryAppState(SETTINGS) == ApplicationState.RUNNING_IN_FOREGROUND);
        assertThat(visible(AppiumBy.androidUIAutomator(SEARCH)).isDisplayed()).isTrue();
    }

    /** On a loaded device the driver gives up after 2 s: the rotation is simply asked for again. */
    private void rotateTo(ScreenOrientation orientation) {
        wait.ignoring(InvalidElementStateException.class).until(d -> {
            driver.rotate(orientation);
            return driver.getOrientation() == orientation;
        });
    }
}
