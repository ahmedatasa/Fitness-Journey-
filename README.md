# Fitness Journey

Arabic-first Android fitness tracker built with Kotlin and Jetpack Compose.

## Current MVP
- User onboarding/profile: age, height, current weight, target weight, activity level, exercise location, experience, food preference.
- Local persistence with Android SharedPreferences; no account or cloud sync required.
- Dashboard, manual step entry, walking/running duration logging, meal/workout notes, and weight entries.
- Arabic interface and responsive Compose layouts.
- GitHub Actions builds a debug APK on pushes to `main` and manual workflow dispatch.

## Build
GitHub Actions workflow: **Build Fitness Journey APK**.
After a successful run, download the `Fitness-Journey-debug-apk` artifact and extract `app-debug.apk`.

## Current limitations (not yet implemented)
- Automatic sensor-based step tracking and GPS route recording.
- Verified food nutrition database and calorie/macronutrient calculations.
- Personalized evidence-informed calorie and exercise plan engine.
- Charts, export/backup, reminders, and comprehensive device testing.

The profile values are entered by each user; the values in code are initial form defaults only. This app is informational and does not replace medical advice.
