# Luxury Pixel 3 Digital Watch Face

Luxury Pixel 3 Digital Watch Face is a Wear OS watch face project with a preview companion app, production watch face module, and Play Store listing assets.

## Project Structure

- `app/` contains the Android preview app and Java renderer implementation.
- `watchface/` contains the Wear OS Watch Face Format package.
- `store-assets/` contains Play Store screenshots, icon, feature graphic, and listing copy.
- `luxury-pixel-privacy/` contains the separate GitHub Pages download hub and public privacy policy HTML.

## Build

Create a local `local.properties` file that points to your Android SDK, then build with Gradle:

```powershell
.\gradlew.bat build
```

Release signing uses `keystore.properties` and a local keystore. Those files are intentionally ignored and should not be committed.
