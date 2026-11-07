package com.servet.appium.base;

import java.nio.file.Files;
import java.nio.file.Path;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.options.UiAutomator2Options;

/** My Demo App: installed from apps/ if needed, its data cleared before every test. */
public abstract class BaseDemoAppTest extends BaseAppiumTest {

    protected static final String PACKAGE = "com.saucelabs.mydemoapp.android";

    private static final Path APK = Path.of("apps", "mda-2.3.0-27.apk").toAbsolutePath();

    @Override
    protected UiAutomator2Options capabilities() {
        if (!Files.exists(APK)) {
            throw new IllegalStateException(APK + " is missing: run scripts/download-demo-app.ps1");
        }
        return new UiAutomator2Options()
                .setApp(APK.toString())
                .setAppPackage(PACKAGE)
                .setAppActivity(".view.activities.SplashActivity")
                .setAppWaitActivity("*.MainActivity")
                .setNoReset(false) // a clean cart and no login in every test
                .setAutoGrantPermissions(true); // the Drawing screen asks for a photo permission
    }

    protected static By id(String resourceId) {
        return AppiumBy.id(PACKAGE + ":id/" + resourceId);
    }

    protected static By description(String contentDescription) {
        return AppiumBy.accessibilityId(contentDescription);
    }

    protected static By text(String visibleText) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + visibleText + "\")");
    }
}
