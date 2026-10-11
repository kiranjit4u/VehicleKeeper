# Vehicle Keeper (Android)

Kotlin + Jetpack Compose + Room + WorkManager.

## Run
1. Open this folder in Android Studio (Koala or newer) and let Gradle sync.
2. Run on a device/emulator (Android 8.0+, API 26).
3. Allow the notification permission when asked (Android 13+).

## Reminders
- A daily WorkManager job (~9:00 AM) checks PUC, Insurance and Road tax dates.
- Notifies when a date is 5 days away or less (also keeps notifying once expired).
- Change the window with `REMINDER_DAYS` in work/Notifier.kt.
- The refresh icon on the home screen runs the check immediately for testing.

## Get an installable APK
- Android Studio: Build > Build Bundle(s) / APK(s) > Build APK(s). File: app/build/outputs/apk/debug/app-debug.apk
- Or push this folder to a GitHub repo; the "Build APK" workflow (Actions tab) produces the APK as a downloadable artifact.
