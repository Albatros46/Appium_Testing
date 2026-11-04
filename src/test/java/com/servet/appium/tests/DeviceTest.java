package com.servet.appium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import com.servet.appium.support.Adb;

/** Is the device ready? If one of these fails, the problem is the emulator, not Appium or the app. */
public class DeviceTest {

    private static final int MINIMUM_API_LEVEL = 30;

    @Test
    public void a_device_is_connected_and_ready() {
        assertThat(Adb.run(List.of("get-state"))).isEqualTo("device");
    }

    @Test
    public void android_has_finished_booting() {
        assertThat(Adb.shell("getprop", "sys.boot_completed")).isEqualTo("1");
    }

    @Test
    public void the_api_level_is_supported() {
        assertThat(Integer.parseInt(Adb.shell("getprop", "ro.build.version.sdk")))
                .isGreaterThanOrEqualTo(MINIMUM_API_LEVEL);
    }

    @Test
    public void the_settings_app_used_by_the_tests_is_installed() {
        assertThat(Adb.shell("pm", "path", "com.android.settings")).startsWith("package:");
    }

    @Test
    public void the_start_activity_of_the_settings_app_is_known() {
        assertThat(Adb.shell("cmd", "package", "resolve-activity", "--brief", "com.android.settings"))
                .endsWith("com.android.settings/.Settings");
    }
}
