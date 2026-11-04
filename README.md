# Appium Testing · Android automation with Java

[![Android Tests](https://github.com/Albatros46/Appium_Testing/actions/workflows/android-tests.yml/badge.svg)](https://github.com/Albatros46/Appium_Testing/actions/workflows/android-tests.yml)
![Java](https://img.shields.io/badge/Java-21-orange) ![Appium](https://img.shields.io/badge/Appium-3.9-662D91) ![UiAutomator2](https://img.shields.io/badge/UiAutomator2-8.7-3DDC84) ![Android](https://img.shields.io/badge/Android-15%20(API%2035)-3DDC84) ![TestNG](https://img.shields.io/badge/TestNG-7.12-CD6E00) ![Tests](https://img.shields.io/badge/tests-13-brightgreen)

Mobile test automation from the ground up: the concepts, a working Appium setup, and 13 tests on a real Android 15 emulator, locally and in GitHub Actions. The tests use the **Android Settings app**, which is on every device, so nothing has to be installed.

Part of my 52-week QA Automation roadmap — **Week 21: mobile test types, Appium architecture, adb, setup**.

## Documentation

| Document | Content |
|---|---|
| [Mobile testing](docs/01-mobile-testing-types.md) | native, hybrid, mobile web · emulator, real device, cloud · mobile-only test types |
| [Appium architecture](docs/02-appium-architecture.md) | how a command travels to the device, session flow, capabilities, locators |
| [adb cheat sheet](docs/03-adb-cheatsheet.md) | device, apps, input, evidence, state changes, port forwarding; every command was run on the emulator |

## Tests

| Class | Tests | Checks |
|---|---|---|
| `DeviceTest` | 5 | device ready, boot finished, API level, target app present (**adb only**: if these fail, the problem is the emulator, not Appium) |
| `SettingsAppTest` | 3 | app starts · About page shows the model `adb` reports · Back key returns |
| `AppLifecycleTest` | 3 | rotation · background and return · killed and restarted |
| `NetworkStateTest` | 2 | airplane mode set with adb is shown in the app · switched in the app is seen by adb |

Two independent channels check each other: **adb changes or reads the device, Appium reads or changes the app.**

### Control run

With the line that switches airplane mode on disabled, the test turned red as it should (`expected: "true" but was: "false"`) and a screenshot appeared in `target/screenshots`. The full suite then passed 3 times in a row (13/13).

## What went wrong, and what it taught me

| Problem | Cause | Fix |
|---|---|---|
| 2 of 3 tests failed after a passing test | `noReset=true` keeps the app open, so the next test started on the page where the previous one ended (the failure screenshot showed it) | `appium:forceAppLaunch=true`: every session restarts the app |
| The airplane switch was not found | my XPath was a guess | read the real screen with `uiautomator dump`; the switch has a stable id, and the row around it takes the tap |
| `sdkmanager` crashed | a Windows application control policy blocks a DLL of the new Android CLI | left the security setting alone; installed the system image from Google's repository, verified by SHA-1 |
| Newest driver refused | `uiautomator2` 9 needs Node 24.15, here 24.13 | pinned 8.7.3 in `package.json` |
| `org.openqa.selenium.support.ui` missing | java-client 11 no longer brings `selenium-support` | added it with the Selenium version Maven resolved |
| `start-emulator.ps1` stopped at once when it was finally run | in Windows PowerShell 5.1, adb's "no emulators found" on stderr became a terminating error under `ErrorActionPreference = 'Stop'` | the wait loop runs with `Continue`; scripts are now tested, not only written |
| Airplane mode left on would break later tests | a failing test skips its last lines | `@AfterMethod(alwaysRun = true)` restores the device |

## Run it

Needs JDK 21+, Maven, Node.js 22+ and the Android SDK (`ANDROID_HOME`) with the image `system-images;android-35;default;x86_64`.

```powershell
npm ci                                                 # Appium and the driver, pinned, no global install
.\scripts\create-emulator.ps1 -AvdHome E:\Android\avd  # once
.\scripts\start-emulator.ps1 -AvdHome E:\Android\avd   # waits for the boot
npm run doctor                                         # checks the setup
npm run appium                                         # keep this terminal open
```

In a second terminal:

```powershell
mvn test                       # all 13 tests
mvn test -Dtest=DeviceTest     # only the device checks, no Appium server needed
```

`-AvdHome` is optional; I use it to keep the virtual disk off my nearly full C: drive.

**CI** (`.github/workflows/android-tests.yml`): enables KVM on the Linux runner, starts an Android 15 emulator, starts the pinned Appium server and runs all tests; the Appium log, reports and failure screenshots are uploaded.

### Measured

| | My laptop | GitHub Actions |
|---|---|---|
| First boot of a new emulator | 14 min (other work running, 2.6 GB RAM free) | 30 s |
| Later boot | 95 s | — |
| All 13 tests | 167 – 270 s (3 runs) | 71 s |

## Structure

```
docs/                          the three documents above
scripts/                       create and start the emulator
package.json                   Appium server and driver, pinned
src/test/java/com/servet/appium/
├── base/BaseAppiumTest        one session per test, screenshot on failure
├── support/Adb                adb commands from Java
└── tests/                     DeviceTest · SettingsAppTest · AppLifecycleTest · NetworkStateTest
```

## Next steps
My Demo App with Page Objects, gestures with W3C Actions, hybrid apps and WebViews, parallel devices with Cucumber, and a device cloud.
