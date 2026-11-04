# Mobile testing: what is different

Web testing asks "does the page work in this browser?". Mobile testing also has to ask what the device, the operating system and the network do to the app while it runs.

## Types of mobile apps

| Type | Built with | Runs in | Automation | Example |
|---|---|---|---|---|
| **Native** | Kotlin/Java, Swift | directly on the OS | UiAutomator2 (Android), XCUITest (iOS) | Android Settings |
| **Hybrid** | native shell + WebView | a browser view inside an app | native context + WEBVIEW context | many banking and shop apps |
| **Mobile web** | HTML/JS | Chrome, Safari on the phone | browser automation | any responsive site |
| **Cross-platform** | Flutter, React Native | own rendering or native widgets | React Native: UiAutomator2; Flutter: special driver | — |

## Emulator, simulator, real device, cloud

| | Good for | Not good for |
|---|---|---|
| **Emulator** (Android, a full virtual device) | fast feedback, CI, many API levels | real performance, sensors, camera, vendor skins |
| **Simulator** (iOS, runs on macOS only) | fast UI feedback | hardware behaviour, push notifications in full |
| **Real device** | truth: performance, battery, network | cost, maintenance, parallel scale |
| **Device cloud** (BrowserStack, Sauce Labs) | many real devices without owning them | cost, slower feedback, data leaves the company |

A healthy pipeline uses emulators on every push and real devices or a cloud before a release.

## Test types that only exist (or matter more) on mobile

| Test type | Question | Example |
|---|---|---|
| **Interruption** | does the app survive being interrupted? | a call, a notification, the app goes to the background and returns |
| **Lifecycle** | is the state kept? | rotate the screen, kill the process, restore |
| **Network** | what happens on a bad connection? | flight mode, 2G, the switch from Wi-Fi to mobile data |
| **Permissions** | what if the user says no? | camera refused, permission withdrawn in the settings |
| **Install and update** | does the data survive? | install, update from the old version, uninstall |
| **Fragmentation** | does it work on the devices users have? | screen sizes, API levels, manufacturer skins |
| **Gestures** | are touch actions recognised? | swipe, long press, pinch, drag and drop |
| **Battery, memory, storage** | how does it behave when resources run out? | low memory, full storage, battery saver |
| **Localisation** | do language and region work? | long German words, date formats, right-to-left layouts |
| **Accessibility** | can everybody use it? | TalkBack, font size, content descriptions |

These map to the ISTQB Mobile Application Testing syllabus (CT-MAT), which is on my roadmap.
