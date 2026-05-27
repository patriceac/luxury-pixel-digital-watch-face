# Luxury Pixel Digital Watch Face

Luxury Pixel Digital Watch Face is the Android/Wear OS source project for the digital Luxury Pixel face.

Public landing page and APK downloads: https://patriceac.github.io/luxury-pixel-watch-faces/

Source repositories:

- Digital source: https://github.com/patriceac/luxury-pixel-digital-watch-face
- Analog source: https://github.com/patriceac/luxury-pixel-analog-watch-face
- Public download site: https://github.com/patriceac/luxury-pixel-watch-faces

## Project Structure

- `app/` contains the Android preview app and Java renderer implementation.
- `watchface/` contains the Wear OS Watch Face Format package.
- `store-assets/` contains Play Store screenshots, icon, feature graphic, and listing copy.

## Build

Create a local `local.properties` file that points to your Android SDK, then build with Gradle:

```powershell
.\gradlew.bat build
```

Release signing uses `keystore.properties` and a local keystore. Those files are intentionally ignored and should not be committed.
