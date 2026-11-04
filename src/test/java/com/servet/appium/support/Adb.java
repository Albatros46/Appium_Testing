package com.servet.appium.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Runs adb commands, to prepare and to check the device independently of Appium. */
public final class Adb {

    private static final String EXECUTABLE = locate();

    private Adb() {
    }

    /** "adb shell <command>" on the connected device; the output without trailing whitespace. */
    public static String shell(String... command) {
        List<String> arguments = new ArrayList<>(List.of("shell"));
        arguments.addAll(List.of(command));
        return run(arguments);
    }

    public static String run(List<String> arguments) {
        List<String> command = new ArrayList<>(List.of(EXECUTABLE));
        command.addAll(arguments);
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("adb timed out: " + arguments);
            }
            if (process.exitValue() != 0) {
                throw new IllegalStateException("adb " + arguments + " failed: " + output);
            }
            return output;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot run " + EXECUTABLE, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /** ANDROID_HOME, then the default location of the Android SDK on Windows, then the PATH. */
    private static String locate() {
        String home = System.getenv("ANDROID_HOME");
        if (home == null || home.isBlank()) {
            String local = System.getenv("LOCALAPPDATA");
            home = local == null ? "" : Path.of(local, "Android", "Sdk").toString();
        }
        Path windows = Path.of(home, "platform-tools", "adb.exe");
        Path unix = Path.of(home, "platform-tools", "adb");
        if (Files.exists(windows)) {
            return windows.toString();
        }
        return Files.exists(unix) ? unix.toString() : "adb";
    }
}
