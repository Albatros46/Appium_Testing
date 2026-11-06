package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseDemoAppTest;

public class CatalogTest extends BaseDemoAppTest {

    @Test(groups = "smoke")
    public void the_catalog_is_the_start_screen() {
        assertThat(visible(text("Products")).isDisplayed()).isTrue();
        assertThat(productNames()).isNotEmpty();
    }

    @Test
    public void products_are_sorted_by_name_at_the_start() {
        visible(id("productTV"));
        List<String> names = productNames();

        assertThat(names).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
    }

    @DataProvider
    public Object[][] sortOrders() {
        return new Object[][] {
                {"Descending order by name"},
                {"Ascending order by price"},
                {"Descending order by price"},
        };
    }

    @Test(dataProvider = "sortOrders")
    public void products_can_be_sorted(String option) {
        visible(id("sortIV")).click();
        visible(description(option)).click();
        wait.until(d -> !productNames().isEmpty());

        switch (option) {
            case "Descending order by name" -> assertThat(productNames())
                    .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER.reversed());
            case "Ascending order by price" -> assertThat(prices()).isSorted();
            default -> assertThat(prices()).isSortedAccordingTo(Comparator.reverseOrder());
        }
    }

    private List<String> productNames() {
        return driver.findElements(id("productTV")).stream().map(WebElement::getText).toList();
    }

    private List<Double> prices() {
        return driver.findElements(id("priceTV")).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "").trim())).toList();
    }
}
