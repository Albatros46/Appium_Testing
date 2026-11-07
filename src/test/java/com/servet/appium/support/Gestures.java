package com.servet.appium.support;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import io.appium.java_client.android.AndroidDriver;

/**
 * Finger gestures as W3C pointer actions: each finger is a pointer with timed moves, presses and releases.
 * The driver plays the fingers of one perform() call at the same time, so two fingers can draw together.
 */
public final class Gestures {

    private static final PointerInput.Kind TOUCH = PointerInput.Kind.TOUCH;
    private static final PointerInput.Origin VIEWPORT = PointerInput.Origin.viewport();
    private static final PointerInput.MouseButton BUTTON = PointerInput.MouseButton.LEFT;
    private static final Duration SEGMENT = Duration.ofMillis(150);
    private static final Duration NO_HOLD = Duration.ZERO;

    private final AndroidDriver driver;

    public Gestures(AndroidDriver driver) {
        this.driver = driver;
    }

    /** Taps the screen once at the given pixel. */
    public void tap(int x, int y) {
        PointerInput finger = new PointerInput(TOUCH, "finger1");
        driver.perform(List.of(stroke(finger, List.of(new Point(x, y)), NO_HOLD, Duration.ofMillis(80))));
    }

    /** Taps the middle of the element. */
    public void tapCenterOf(WebElement element) {
        Rectangle rect = element.getRect();
        tap(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
    }

    /** Drags one finger from one point to another in the given time. */
    public void swipe(Point from, Point to, Duration duration) {
        PointerInput finger = new PointerInput(TOUCH, "finger1");
        driver.perform(List.of(stroke(finger, List.of(from, to), duration, Duration.ofMillis(100))));
    }

    /** Swipes up inside the area: the content moves up, so the lower part of the list comes into view. */
    public void swipeUp(Rectangle area) {
        swipeVertically(area, 0.8, 0.2);
    }

    /** Swipes down inside the area: the reverse of swipeUp. */
    public void swipeDown(Rectangle area) {
        swipeVertically(area, 0.2, 0.8);
    }

    /** Moves one finger through all the points, 150 ms per segment. */
    public void draw(List<Point> path) {
        PointerInput finger = new PointerInput(TOUCH, "finger1");
        driver.perform(List.of(stroke(finger, path, SEGMENT, NO_HOLD)));
    }

    /** Moves two fingers at the same time, each through its own path; both paths need the same number of points. */
    public void drawTogether(List<Point> first, List<Point> second) {
        if (first.size() != second.size()) {
            throw new IllegalArgumentException(
                    "both paths need the same number of points: " + first.size() + " and " + second.size());
        }
        PointerInput finger1 = new PointerInput(TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(TOUCH, "finger2");
        driver.perform(List.of(
                stroke(finger1, first, SEGMENT, NO_HOLD),
                stroke(finger2, second, SEGMENT, NO_HOLD)));
    }

    private void swipeVertically(Rectangle area, double fromFraction, double toFraction) {
        int x = area.getX() + area.getWidth() / 2;
        int fromY = area.getY() + (int) (area.getHeight() * fromFraction);
        int toY = area.getY() + (int) (area.getHeight() * toFraction);
        swipe(new Point(x, fromY), new Point(x, toY), Duration.ofMillis(600));
    }

    /**
     * Press at the first point, move through the rest, release. A hold pauses after the press and,
     * for paths with more than one point, again at the last point before the release.
     */
    private static Sequence stroke(PointerInput finger, List<Point> path, Duration segment, Duration hold) {
        Sequence sequence = new Sequence(finger, 0);
        Point start = path.get(0);
        sequence.addAction(finger.createPointerMove(Duration.ZERO, VIEWPORT, start.getX(), start.getY()));
        sequence.addAction(finger.createPointerDown(BUTTON.asArg()));
        addHold(sequence, finger, hold);
        for (Point point : path.subList(1, path.size())) {
            sequence.addAction(finger.createPointerMove(segment, VIEWPORT, point.getX(), point.getY()));
        }
        if (path.size() > 1) {
            addHold(sequence, finger, hold);
        }
        sequence.addAction(finger.createPointerUp(BUTTON.asArg()));
        return sequence;
    }

    private static void addHold(Sequence sequence, PointerInput finger, Duration hold) {
        if (!hold.isZero()) {
            sequence.addAction(new Pause(finger, hold));
        }
    }
}
