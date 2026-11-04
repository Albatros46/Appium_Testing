package com.servet.appium.base;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

/** One Appium session per test, a screenshot in target/screenshots when a test fails. */
public abstract class BaseAppiumTest {

    protected static final String SETTINGS = "com.android.settings";

    private static final String APPIUM_URL = System.getProperty("appium.url", "http://127.0.0.1:4723");

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void startSession() throws URISyntaxException, IOException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setAppPackage(SETTINGS)
                .setAppActivity(".Settings")
                .setNoReset(true)
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setUiautomator2ServerInstallTimeout(Duration.ofMinutes(3))
                .setAdbExecTimeout(Duration.ofMinutes(1));
        driver = new AndroidDriver(new URI(APPIUM_URL).toURL(), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod(alwaysRun = true)
    public void endSession(ITestResult result) throws IOException {
        if (driver == null) {
            return;
        }
        try {
            if (!result.isSuccess()) {
                Path folder = Files.createDirectories(Path.of("target", "screenshots"));
                Files.write(folder.resolve(result.getName() + ".png"), driver.getScreenshotAs(OutputType.BYTES));
            }
        } finally {
            driver.quit();
            driver = null;
        }
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
