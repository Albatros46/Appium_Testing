package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseDemoAppTest;

/** The accounts are the public demo accounts printed on the app's own login screen. */
public class LoginTest extends BaseDemoAppTest {

    private static final String USER = "bod@example.com";
    private static final String LOCKED_USER = "alice@example.com";
    private static final String PASSWORD = "10203040";

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "startSession")
    public void openTheLoginScreen() {
        visible(id("menuIV")).click();
        visible(text("Log In")).click();
        visible(id("loginBtn"));
    }

    private void logIn(String user, String password) {
        visible(id("nameET")).sendKeys(user);
        visible(id("passwordET")).sendKeys(password);
        visible(id("loginBtn")).click();
    }

    @Test(groups = "smoke")
    public void a_valid_user_reaches_the_catalog_and_can_log_out() {
        logIn(USER, PASSWORD);

        visible(text("Products"));
        visible(id("menuIV")).click();
        assertThat(visible(text("Log Out")).isDisplayed()).isTrue();
    }

    /**
     * KNOWN BUG MDA-01: an address that is not in the list of users is accepted. This test pins the
     * behaviour; it turns red when the app starts to refuse such a login, and then it must change.
     */
    @Test
    public void known_bug_mda_01_an_unregistered_user_is_logged_in() {
        logIn("not-registered@example.com", PASSWORD);

        visible(text("Products"));
        visible(id("menuIV")).click();
        assertThat(visible(text("Log Out")).isDisplayed()).isTrue();
    }

    @DataProvider
    public Object[][] invalidLogins() {
        return new Object[][] {
                {"", PASSWORD, "nameErrorTV", "Username is required"},
                {USER, "", "passwordErrorTV", "Enter Password"},
                {LOCKED_USER, PASSWORD, "passwordErrorTV", "Sorry this user has been locked out."},
        };
    }

    @Test(dataProvider = "invalidLogins")
    public void an_invalid_login_shows_the_reason(String user, String password, String errorField, String message) {
        logIn(user, password);

        assertThat(visible(id(errorField)).getText()).isEqualTo(message);
    }
}
