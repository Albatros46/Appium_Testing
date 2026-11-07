package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.servet.appium.base.BaseDemoAppTest;
import com.servet.appium.support.Gestures;

public class GestureTest extends BaseDemoAppTest {

    private Gestures gestures;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "startSession")
    public void createGestures() {
        gestures = new Gestures(driver);
    }

    @Test
    public void swiping_up_scrolls_the_catalog_and_swiping_down_returns() {
        String firstTitle = visible(id("titleTV")).getText();

        gestures.swipeUp(visible(id("productRV")).getRect());
        wait.until(d -> !firstTitle.equals(d.findElement(id("titleTV")).getText()));
        assertThat(visible(id("titleTV")).getText()).isNotEqualTo(firstTitle);

        for (int i = 0; i < 3 && !firstTitle.equals(visible(id("titleTV")).getText()); i++) {
            gestures.swipeDown(visible(id("productRV")).getRect());
        }
        assertThat(visible(id("titleTV")).getText()).isEqualTo(firstTitle);
    }

    @Test
    public void swiping_up_reaches_the_end_of_the_catalog() {
        Set<String> titles = new LinkedHashSet<>(visibleTitles());
        boolean reachedEnd = false;

        for (int i = 0; i < 20; i++) {
            int before = titles.size();
            gestures.swipeUp(visible(id("productRV")).getRect());
            titles.addAll(visibleTitles());
            if (titles.size() == before) {
                reachedEnd = true;
                break;
            }
        }

        assertThat(reachedEnd).as("no new products after a swipe").isTrue();
        assertThat(titles).hasSizeGreaterThan(4);
    }

    @Test(groups = "smoke")
    public void a_tap_on_a_star_opens_the_review_dialog_and_continue_closes_it() {
        visible(id("productIV")).click();
        gestures.tapCenterOf(visible(id("start4IV")));

        assertThat(visible(id("sortTV")).getText()).isEqualTo("Thank you for submitting your review!");
        visible(id("closeBt")).click();
        wait.until(d -> d.findElements(id("sortTV")).isEmpty());
    }

    @Test
    public void drawing_with_one_finger_changes_the_pad_and_clear_empties_it() {
        openDrawing();
        BufferedImage empty = padImage();
        Rectangle pad = visible(id("signature_pad")).getRect();

        untilThePadChanges(empty, () -> gestures.draw(zigzag(pad)));

        visible(id("clearBtn")).click();
        wait.until(d -> !differs(empty, padImage()));
    }

    /** The pad is a single-touch view: the second finger is sent, but only the first one leaves a line. */
    @Test
    public void the_pad_follows_only_the_first_of_two_fingers() {
        openDrawing();
        BufferedImage empty = padImage();
        Rectangle pad = visible(id("signature_pad")).getRect();
        int top = pad.getY() + pad.getHeight() / 5;
        int bottom = pad.getY() + pad.getHeight() * 4 / 5;
        List<Point> leftLine = verticalLine(pad.getX() + pad.getWidth() / 6, top, bottom);
        List<Point> rightLine = verticalLine(pad.getX() + pad.getWidth() * 5 / 6, top, bottom);

        untilThePadChanges(empty, () -> gestures.drawTogether(leftLine, rightLine));

        BufferedImage after = padImage();
        int half = after.getWidth() / 2;
        assertThat(changedPixels(empty, after, 0, half)).isGreaterThan(0);
        assertThat(changedPixels(empty, after, half, after.getWidth())).isZero();
    }

    private void openDrawing() {
        visible(id("menuIV")).click();
        visible(text("Drawing")).click();
    }

    /** Touches right after the screen opens can be lost, so the stroke is repeated until the pad shows it. */
    private void untilThePadChanges(BufferedImage empty, Runnable stroke) {
        wait.until(d -> {
            stroke.run();
            return differs(empty, padImage());
        });
    }

    private List<String> visibleTitles() {
        return wait.until(d -> d.findElements(id("titleTV")).stream().map(WebElement::getText).toList());
    }

    private BufferedImage padImage() {
        byte[] png = visible(id("signature_pad")).getScreenshotAs(OutputType.BYTES);
        try {
            return ImageIO.read(new ByteArrayInputStream(png));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Five points zigzagging down the pad, kept inside a margin of a tenth of its width. */
    private static List<Point> zigzag(Rectangle pad) {
        int margin = pad.getWidth() / 10;
        int left = pad.getX() + margin;
        int right = pad.getX() + pad.getWidth() - margin;
        int top = pad.getY() + margin;
        int height = pad.getHeight() - 2 * margin;
        return List.of(
                new Point(left, top),
                new Point(right, top + height / 4),
                new Point(left, top + height / 2),
                new Point(right, top + height * 3 / 4),
                new Point(left, top + height));
    }

    /** Four points on one vertical line from top to bottom. */
    private static List<Point> verticalLine(int x, int top, int bottom) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            points.add(new Point(x, top + (bottom - top) * i / 3));
        }
        return points;
    }

    private static boolean differs(BufferedImage a, BufferedImage b) {
        return changedPixels(a, b, 0, a.getWidth()) > 0;
    }

    private static int changedPixels(BufferedImage a, BufferedImage b, int fromX, int toX) {
        int changed = 0;
        for (int x = fromX; x < toX; x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    changed++;
                }
            }
        }
        return changed;
    }
}
