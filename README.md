# GRUMP — v0.1.0

**General Response Unit for Media Pessimism.** A small, disappointed goblin who lives on your Android home screen and judges humanity when poked.

## What works

- Native Kotlin home-screen widget, approximately 2×2 cells; resizable in both directions.
- Original green goblin vector art: idle, annoyed, and exasperated (with a drawn sigh).
- Tap the creature or bubble for one of **100 unique offline comments**, with no immediate repeats.
- Technology, politics, corporate nonsense, science, humanity, and existential despair.
- Independent saved comment and expression for each widget; restored when Android updates/recreates it.
- No permissions, network access, accounts, ads, telemetry, polling, or runtime cloud dependencies.
- Minimal app containing installation and widget instructions.

## Get the APK on your phone

No Android Studio or Pydroid is needed. GitHub builds the app.

1. Open [Actions → Android](https://github.com/TAM-Hiding/GRUMP/actions/workflows/android.yml) in your browser and sign in to GitHub. Use the browser's desktop-site option if artifacts are hidden in the mobile view.
2. Choose a successful run for the feature branch/PR (or `main` after merge). Check the commit matches what you want to install.
3. Scroll to **Artifacts**, download **GRUMP-debug-apk**, and extract the ZIP using Files. Inside is `app-debug.apk`.
4. Tap the APK. If prompted, allow **Install unknown apps** for the browser or Files app used to open it, then install. You may turn that permission off afterward.
5. Open GRUMP once to read its instructions.
6. On the Pixel home screen, long-press empty space → **Widgets** → **GRUMP**. Drag him onto the home screen.
7. Tap him. He has opinions. Long-press and drag the resize handles for more room.

Artifacts expire after GitHub's retention period. Re-run the workflow if necessary. Workflow artifacts require a signed-in GitHub account.

These are debug builds signed with the build runner's disposable debug key. Different runs may have different keys: if Android refuses an update due to a signature mismatch, uninstall the old build first (this removes widgets and saved moods). No signing keys are committed. Stable release signing is future work.

## Build and validation

JDK 17, Android SDK platform 36 / build tools 36.0.0, AGP 8.13.0, Kotlin 2.2.10, Gradle 8.13. The standard Gradle wrapper is committed with a distribution checksum; no local Gradle installation is required.

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

CI runs compilation, lint, unit tests, then Android 15 emulator instrumentation. Tests check widget discovery and binding, real RemoteViews click broadcasts, independent persistent states, expression cycling, comment selection, and all bubbles at the minimum supported size with normal/large fonts. Emulator test reports are separate artifacts. Local emulator testing requires granting the host test permission after installing the app:

```sh
adb shell appwidget grantbind --package dev.strgzr.grump
./gradlew :app:connectedDebugAndroidTest
```

Physical Pixel installation, launcher appearance, resizing, and offline interactions still require device acceptance testing. A successful CI build is not proof of those device checks.

## Architecture and limits

`GrumpWidgetProvider` handles explicit immutable per-widget PendingIntents and RemoteViews. `GrumpPreferences` owns state by widget ID, removes it on deletion, and remaps IDs on restore. `CommentEngine` is a pure local text source, separated from the renderer for later text providers. There are no services, scheduled jobs, or periodic widget updates.

Minimum Android 12 (API 31); compile/target Android 16 (API 36). Android 15–17 are intended, but Android 17 and the Pixel launcher require physical verification. Launcher grid dimensions vary; requested 2×2 sizing is a hint, and the minimum readable dimensions can require more cells on some grids. Bubble text auto-sizes; enlarge the widget if you prefer larger lettering. RTL layout is supported, but v0.1 text is English only.

The speech bubble stays visible until the next tap. Expressions cycle rather than animate continuously. The sigh is drawn in the exasperated expression; there is no audio or timed animation. Reboot/update retains saved moods; uninstall clears them. Android backup is disabled.

## Roadmap (not implemented)

- **v0.2:** opt-in RSS feeds, headline cache, categories, offline fallback.
- **v0.3:** investigate Gemini Nano via AICore / ML Kit GenAI, checking device support and foreground/API restrictions. Supply short headline-based comments to the renderer; keep local fallback. Cloud inference only with explicit user opt-in.
- **v0.4:** extra expressions, optional idle behaviors, adjustable sarcasm, voice/sighs, more interactions.

Build the smallest reliable version first. Do not overengineer the little bastard.
