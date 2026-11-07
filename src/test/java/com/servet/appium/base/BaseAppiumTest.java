package com.servet.appium.base;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

/** One Appium session per test, a screenshot in target/screenshots when a test fails; for any app. */
public abstract class BaseAppiumTest {

    private static final String APPIUM_URL = System.getProperty("appium.url", "http://127.0.0.1:4723");

    /** A cold app start on a loaded device or a CI runner can take longer than 15 s. */
    private static final Duration WAIT = Duration.ofSeconds(Long.getLong("wait.seconds", 30));

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    /** The app under test and how it starts: the part that differs between the apps. */
    protected abstract UiAutomator2Options capabilities();

    @BeforeMethod(alwaysRun = true)
    public void startSession() throws URISyntaxException, IOException {
        UiAutomator2Options options = capabilities()
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setUiautomator2ServerInstallTimeout(Duration.ofMinutes(3))
                .setAdbExecTimeout(Duration.ofMinutes(1));
        driver = new AndroidDriver(new URI(APPIUM_URL).toURL(), options);
        wait = new WebDriverWait(driver, WAIT);
        wait.ignoring(StaleElementReferenceException.class); // lists are redrawn while they are read
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
        return wait.until(d -> {
            dismissNotResponding(d);
            return d.findElements(locator).stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
        });
    }

    // on a loaded machine Android shows this dialog over every app, so tap its Wait button before looking
    private static void dismissNotResponding(WebDriver driver) {
        List<WebElement> dialogs = driver.findElements(AppiumBy.id("android:id/aerr_wait"));
        if (!dialogs.isEmpty()) {
            dialogs.get(0).click();
        }
    }
}
