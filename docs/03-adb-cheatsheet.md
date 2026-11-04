# adb cheat sheet

`adb` (Android Debug Bridge) is how the PC talks to a device. Appium uses it too, so a problem visible in `adb` is not an Appium problem. Every command below was run on the Android 15 emulator of this project.

## Device

| Command | Does |
|---|---|
| `adb devices` | lists devices; `device` = ready, `offline`, `unauthorized` = not usable |
| `adb get-state` | the state of the one connected device |
| `adb shell getprop ro.build.version.release` | Android version (`15`) |
| `adb shell getprop ro.build.version.sdk` | API level (`35`) |
| `adb shell getprop sys.boot_completed` | `1` when the boot has finished; wait for it in CI |
| `adb shell wm size` · `wm density` | screen size (`1080x2400`) and density (`420`) |
| `adb shell dumpsys battery` | battery level and status |

## Apps

| Command | Does |
|---|---|
| `adb install -r app.apk` | installs or updates an APK |
| `adb shell pm list packages` | all installed packages |
| `adb shell pm path com.android.settings` | where the APK is |
| `adb shell cmd package resolve-activity --brief com.android.settings` | package and start activity (the two capabilities Appium needs) |
| `adb shell am start -n com.android.settings/.Settings` | starts an activity |
| `adb shell am force-stop com.android.settings` | stops the app |
| `adb shell monkey -p com.android.settings -c android.intent.category.LAUNCHER 1` | starts an app without knowing its activity |
| `adb shell "dumpsys activity activities \| grep topResumedActivity"` | which screen is in front |

## Input

| Command | Does |
|---|---|
| `adb shell input keyevent KEYCODE_HOME` | Home; also `KEYCODE_BACK` |
| `adb shell input tap 500 1200` | tap at x, y |
| `adb shell input swipe 500 1800 500 600 300` | swipe up in 300 ms |

## Evidence

| Command | Does |
|---|---|
| `adb shell screencap -p /sdcard/s.png` + `adb pull /sdcard/s.png` | screenshot to the PC |
| `adb shell uiautomator dump /sdcard/ui.xml` | the screen as XML: resource ids, texts, bounds |
| `adb logcat -d -t 100` | the last 100 log lines, then exit |

## Changing the device state (for mobile test types)

| Command | Tests |
|---|---|
| `adb shell cmd connectivity airplane-mode enable` / `disable` | offline behaviour |
| `adb shell settings put system font_scale 1.3` (back: `1.0`) | large fonts, accessibility |
| `adb shell wm size 720x1280` (back: `wm size reset`) | a smaller screen on the same emulator |

## Port forwarding

| Command | Does |
|---|---|
| `adb forward tcp:8201 tcp:6790` | PC port to device port; this is how the driver reaches its helper |
| `adb forward --list` · `--remove-all` | shows or removes the forwards |
