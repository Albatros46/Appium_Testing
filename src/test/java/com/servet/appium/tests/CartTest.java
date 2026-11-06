package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseDemoAppTest;

public class CartTest extends BaseDemoAppTest {

    private String productName;
    private String unitPrice;

    /** Runs after the session of the base class started: one product, twice, then the cart. */
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "startSession")
    public void twoOfTheFirstProductInTheCart() {
        productName = visible(id("titleTV")).getText();
        unitPrice = visible(id("priceTV")).getText();
        visible(id("productIV")).click();
        visible(id("plusIV")).click();
        wait.until(d -> "2".equals(d.findElement(id("noTV")).getText()));
        visible(id("cartBt")).click();
        visible(id("cartIV")).click();
        visible(id("removeBt"));
    }

    @Test(groups = "smoke")
    public void the_cart_shows_the_product_and_the_quantity() {
        assertThat(visible(id("titleTV")).getText()).isEqualTo(productName);
        assertThat(visible(id("noTV")).getText()).isEqualTo("2");
        assertThat(visible(id("itemsTV")).getText()).isEqualTo("2 Items");
    }

    @Test
    public void the_total_is_price_times_quantity() {
        double expected = amount(unitPrice) * 2;

        assertThat(amount(visible(id("totalPriceTV")).getText()))
                .isCloseTo(expected, within(0.001));
    }

    @Test
    public void removing_the_item_empties_the_cart() {
        visible(id("removeBt")).click();

        assertThat(visible(text("No Items")).isDisplayed()).isTrue();
        assertThat(driver.findElements(id("cartTV"))).isEmpty();
    }

    private static double amount(String price) {
        return Double.parseDouble(price.replace("$", "").trim());
    }
}
