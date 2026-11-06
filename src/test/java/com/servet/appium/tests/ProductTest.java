package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.servet.appium.base.BaseDemoAppTest;

public class ProductTest extends BaseDemoAppTest {

    @Test(groups = "smoke")
    public void the_detail_page_shows_the_product_chosen_in_the_catalog() {
        String name = visible(id("titleTV")).getText();
        String price = visible(id("priceTV")).getText();

        visible(id("productIV")).click();
        visible(id("cartBt"));

        assertThat(visible(id("productTV")).getText()).isEqualTo(name);
        assertThat(visible(id("priceTV")).getText()).isEqualTo(price);
    }

    @Test
    public void the_quantity_can_be_raised_and_lowered() {
        visible(id("productIV")).click();
        assertThat(visible(id("noTV")).getText()).isEqualTo("1");

        visible(id("plusIV")).click();
        wait.until(d -> "2".equals(d.findElement(id("noTV")).getText()));

        visible(id("minusIV")).click();
        wait.until(d -> "1".equals(d.findElement(id("noTV")).getText()));
    }

    @Test
    public void the_cart_badge_appears_only_after_adding() {
        visible(id("productIV")).click();
        visible(id("cartBt"));
        assertThat(driver.findElements(id("cartTV"))).isEmpty();

        visible(id("plusIV")).click();
        visible(id("cartBt")).click();

        assertThat(visible(id("cartTV")).getText()).isEqualTo("2");
    }
}
