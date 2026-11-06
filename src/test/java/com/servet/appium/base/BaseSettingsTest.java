package com.servet.appium.base;

import io.appium.java_client.android.options.UiAutomator2Options;

/** The preinstalled Android Settings app; its state is the device's state, so it is not reset. */
public abstract class BaseSettingsTest extends BaseAppiumTest {

    protected static final String SETTINGS = "com.android.settings";

    @Override
    protected UiAutomator2Options capabilities() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setAppPackage(SETTINGS)
                .setAppActivity(".Settings")
                .setNoReset(true);
        // noReset alone leaves the app on the screen of the previous test: every test must start clean
        options.amend("appium:forceAppLaunch", true);
        return options;
    }
}
