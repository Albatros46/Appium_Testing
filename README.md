# Appium Testing · Android automation with Java

[![Android Tests](https://github.com/Albatros46/Appium_Testing/actions/workflows/android-tests.yml/badge.svg)](https://github.com/Albatros46/Appium_Testing/actions/workflows/android-tests.yml)
![Java](https://img.shields.io/badge/Java-21-orange) ![Appium](https://img.shields.io/badge/Appium-3.9-662D91) ![UiAutomator2](https://img.shields.io/badge/UiAutomator2-8.7-3DDC84) ![Android](https://img.shields.io/badge/Android-15%20(API%2035)-3DDC84) ![TestNG](https://img.shields.io/badge/TestNG-7.12-CD6E00) ![Tests](https://img.shields.io/badge/tests-34-brightgreen)

Mobile test automation from the ground up: the concepts, a working Appium setup, and 34 tests on a real Android 15 emulator, locally and in GitHub Actions. They run against the preinstalled **Android Settings app** and against Sauce Labs' **[My Demo App](https://github.com/saucelabs/my-demo-app-android)**, a shop app made for test automation.

Part of my 52-week QA Automation roadmap:

| Week | Topic | Release |
|---|---|---|
| 21 | Mobile test types, Appium architecture, adb, setup | [v1.0.0](https://github.com/Albatros46/Appium_Testing/releases/tag/v1.0.0) |
| 22 | Appium Java client + TestNG on My Demo App | [v1.1.0](https://github.com/Albatros46/Appium_Testing/releases/tag/v1.1.0) |
| 23 | Gestures with W3C Actions, stability fixes | [v1.2.0](https://github.com/Albatros46/Appium_Testing/releases/tag/v1.2.0) |

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
| `CatalogTest` | 5 | start screen · default order · 3 sort orders with a data provider |
| `ProductTest` | 3 | detail page matches the catalog · quantity · cart badge |
| `CartTest` | 3 | content · total is price × quantity · removing an item |
| `LoginTest` | 5 | valid login · 3 invalid logins with a data provider · known bug MDA-01 |
| `GestureTest` | 5 | swipe up and back · end of the list · tap on a rating star · one-finger drawing and Clear · two fingers on a single-touch pad |

Two independent channels check each other: **adb changes or reads the device, Appium reads or changes the app.**

TestNG features used: **data providers** (sort orders, invalid logins), **groups** (`mvn test -Dgroups=smoke` runs 4 tests), `@BeforeMethod` with `dependsOnMethods` for preconditions after the session start.

Each My Demo App test starts with **cleared app data** (`noReset=false`), so no test sees the cart or the login of another. Settings keeps its state, because it shows the state of the device.

### Gestures with W3C Actions

`support/Gestures` describes each finger as a W3C pointer: timed moves, press and release. Fingers in one `perform()` call run at the same time. The drawing tests compare screenshots of the pad pixel by pixel, because a drawn line has no element to read.

### Control runs

| Change | Result |
|---|---|
| line that switches airplane mode on disabled | red: `expected: "true" but was: "false"`, screenshot saved |
| cart total expected as price × 3 | red: `59.98` is not close to `89.97`, screenshot saved |
| the drawing stroke removed | red: the pad never changes, the wait times out |

### Defect found

| ID | Finding | Test |
|---|---|---|
| **MDA-01** | An address that is not a registered user (`not-registered@example.com`) with any password is **logged in**: no error, the menu shows "Log Out". Locked users and empty fields are handled correctly. | `known_bug_mda_01_…` pins the behaviour and turns red once the app refuses such a login |
| **MDA-02** | The drawing pad is single-touch: of two fingers on the pad only the first one leaves a line. | `the_pad_follows_only_the_first_of_two_fingers` pins the behaviour |

## What went wrong, and what it taught me

| Problem | Cause | Fix |
|---|---|---|
| 2 of 3 tests failed after a passing test | `noReset=true` keeps the app open, so the next test started on the page where the previous one ended (the failure screenshot showed it) | `appium:forceAppLaunch=true`: every session restarts the app |
| The airplane switch was not found | my XPath was a guess | read the real screen with `uiautomator dump`; the switch has a stable id, and the row around it takes the tap |
| `sdkmanager` crashed | a Windows application control policy blocks a DLL of the new Android CLI | left the security setting alone; installed the system image from Google's repository, verified by SHA-1 |
| Newest driver refused | `uiautomator2` 9 needs Node 24.15, here 24.13 | pinned 8.7.3 in `package.json` |
| `org.openqa.selenium.support.ui` missing | java-client 11 no longer brings `selenium-support` | added it with the Selenium version Maven resolved |
| `start-emulator.ps1` stopped at once when it was finally run | in Windows PowerShell 5.1, adb's "no emulators found" on stderr became a terminating error under `ErrorActionPreference = 'Stop'` | the wait loop runs with `Continue`; scripts are now tested, not only written |
| Sorting tests were green but proved nothing | `productTV` is the page title, not the products: the list had one element, and one element is always sorted | names are in `titleTV`; every list check now requires more than one element |
| Expected an error for an unknown user | I had assumed the message; the app logs the user in | looked at the real screen, documented it as MDA-01 |
| The CI turned red on a commit that changed only the README | same code, another result: a flaky suite. The failure screenshot of the same test locally showed the app still on its splash screen after 15 s | explicit wait raised to 30 s (`-Dwait.seconds`) |
| `queryAppState` right after `activateApp` was sometimes wrong | the call returns before the app is in front | wait for the state instead of asserting it at once |
| Rotation failed with "cannot be changed after 2000ms" | the driver gives up after 2 s on a loaded device | the rotation is requested again inside a wait |
| A stroke right after opening the Drawing screen left nothing | early touches can be lost | the stroke is repeated until the pad changes; the assertion stays strict |
| Android's "isn't responding" dialog covered the app | a system dialog on a loaded machine | `visible()` taps its Wait button while polling |
| Airplane mode left on would break later tests | a failing test skips its last lines | `@AfterMethod(alwaysRun = true)` restores the device |

## Run it

Needs JDK 21+, Maven, Node.js 22+ and the Android SDK (`ANDROID_HOME`) with the image `system-images;android-35;default;x86_64`.

```powershell
npm ci                                                 # Appium and the driver, pinned, no global install
.\scripts\download-demo-app.ps1                        # My Demo App, SHA-256 checked
.\scripts\create-emulator.ps1 -AvdHome E:\Android\avd  # once
.\scripts\start-emulator.ps1 -AvdHome E:\Android\avd   # waits for the boot
npm run doctor                                         # checks the setup
npm run appium                                         # keep this terminal open
```

In a second terminal:

```powershell
mvn test                       # all 34 tests
mvn test -Dgroups=smoke        # the 5 most important My Demo App tests
mvn test -Dtest=DeviceTest     # only the device checks, no Appium server needed
```

`-AvdHome` is optional; I use it to keep the virtual disk off my nearly full C: drive.

**CI** (`.github/workflows/android-tests.yml`): enables KVM on the Linux runner, starts an Android 15 emulator, starts the pinned Appium server and runs all tests; the Appium log, reports and failure screenshots are uploaded.

### Measured

| | My laptop | GitHub Actions |
|---|---|---|
| First boot of a new emulator | 14 min (other work running, 2.6 GB RAM free) | 30 s |
| Later boot | 95 s | — |
| Settings tests only (13) | 167 – 270 s (3 runs) | 71 s |
| All 34 tests | 688 – 872 s | 250 s |

## Structure

```
docs/                          the three documents above
scripts/                       create and start the emulator
package.json                   Appium server and driver, pinned
src/test/java/com/servet/appium/
├── base/BaseAppiumTest        one session per test, screenshot on failure
├── base/BaseSettingsTest · BaseDemoAppTest   capabilities of each app
├── support/Adb · Gestures      adb commands and W3C finger gestures from Java
└── tests/                     Device · SettingsApp · AppLifecycle · NetworkState · Catalog · Product · Cart · Login · Gesture
```

## Next steps
Page Objects and a platform-independent framework, hybrid apps and WebViews, parallel devices with Cucumber, and a device cloud.
