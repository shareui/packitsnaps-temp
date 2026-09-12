# ktpackit Structure

## 1. Kotlin (`PackIt/src/kotlin/`)
* `sh/packit/core/` -> `Core.dex` (non-composable logic):
  * [`stickers/TelegramStickerLoader.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/stickers/TelegramStickerLoader.kt): sticker loader, caching, placeholder.
  * [`info/ClientInfo.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/info/ClientInfo.kt): client fork detection.
  * [`bridge/NavigationBridge.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/bridge/NavigationBridge.kt): url opening.
  * [`state/CoreState.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/state/CoreState.kt): state flags.
* `sh/packit/compose/` -> `Compose.dex` (Jetpack Compose UI):
  * [`ComposeEntry.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/ComposeEntry.kt): `createView()` entrypoint for ComposeFragment.
  * [`activities/MainSettings.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/activities/MainSettings.kt): settings screen.
  * `components/`: UI items ([`TelegramSticker.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/components/TelegramSticker.kt), `SettingsCard`, `SettingsItem`, `SettingsHeader`, `SettingsFooter`, `SettingsDivider`, `SettingsSectionTitle`).
* `stubs/`: compile-time stubs (`org/telegram/*`, `de/shareui/*`).

## 2. Python (`PackIt/src/python/`)
* [`BasePlugin.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/python/BasePlugin.py): plugin entry and `open_settings()`.
* [`Main.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/python/Main.py): lifecycle callbacks.
* [`core/DexLoader.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/python/core/DexLoader.py): `Core.dex` loader with `DelegateLastClassLoader`.
* [`ui/SettingsScreen.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/python/ui/SettingsScreen.py): opens `ComposeFragment` (`Compose.dex` + parent `CoreLoader`).

## 3. Build & Tools
* `tools/compile/`: headless Gradle project for compiling Compose to DEX.
* [`cruel/builds/hooks/compile_core.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/cruel/builds/hooks/compile_core.py): compiles `sh.packit.core` via `kotlinc` + `d8` -> `Core.dex`.
* [`cruel/builds/hooks/compile_compose.py`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/cruel/builds/hooks/compile_compose.py): syncs compose to `tools/compile`, runs `./gradlew --no-daemon :app:dexBuilderRelease`, merges compose DEX -> `Compose.dex`.
* `cruel/local/cache/kotlin/`: DEX and toolchain cache.
* `PackIt/res/assets/`: temporary destination for `Core.dex` and `Compose.dex` (cleaned after pack).
