# ktpackit Structure

## 1. Kotlin (`PackIt/src/kotlin/`)
* `sh/packit/core/` -> `Core.dex` (non-composable logic):
  * [`stickers/TelegramStickerLoader.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/stickers/TelegramStickerLoader.kt): sticker loader, caching, placeholder.
  * [`info/ClientInfo.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/info/ClientInfo.kt): client fork detection.
  * [`bridge/NavigationBridge.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/bridge/NavigationBridge.kt): url opening constants and bridge.
  * [`utils/OpenUrl.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/utils/OpenUrl.kt): URL navigation helpers (`openUrlInApp` via `Browser.openUrl` for Telegram in-app links, `openUrlInBrowser` for external links).
  * [`state/CoreState.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/state/CoreState.kt): state flags, dynamic `pluginVersion` lookup via `de.shareui.exterasdk.metadata.Metadata`.
  * [`ui/RestartRequired.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/core/ui/RestartRequired.kt): app restart notification bulletin with retry logic.
* `sh/packit/compose/` -> `Compose.dex` (Jetpack Compose UI):
  * [`ComposeEntry.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/ComposeEntry.kt): `createView()` entrypoint for ComposeFragment.
  * [`activities/MainSettings.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/activities/MainSettings.kt): settings screen.
  * `components/`: UI items ([`TelegramSticker.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/sh/packit/compose/components/TelegramSticker.kt), `TextCloud`, `SettingsItem`, `SettingsHeader`, `SettingsFooter`, `SettingsSectionTitle`, `ExpressiveSettingsGroup`, `ExpressiveShape`, `ExpressivePalette`, `ExpressiveBounce` spring animations).
* `stubs/`: compile-time stubs:
  * `org/telegram/*`: Telegram client APIs.
  * `de/shareui/composeshell/*`: Compose theme bridge.
  * `de/shareui/exterasdk/*`: KotlinSDK stubs ([`metadata/Metadata.kt`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/src/kotlin/stubs/de/shareui/exterasdk/metadata/Metadata.kt), `localization/Strings.kt`, `ui/BulletinHelper.kt`, `utils/AndroidUtils.java`).

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

## 4. KotlinSDK Integration
* Plugin relies on `kotlinsdk` (`de.shareui.exterasdk.*` injected into base `ClassLoader` by `KotlinSDK` plugin):
  * [`de.shareui.exterasdk.metadata.Metadata`](file:///home/shareui/dev/extera/plugins-dev/plugins/sdkkt/KotlinSDK/src/kotlin/de/shareui/exterasdk/metadata/Metadata.kt): dynamic metadata and version retrieval.
  * [`de.shareui.exterasdk.localization.Strings`](file:///home/shareui/dev/extera/plugins-dev/plugins/sdkkt/KotlinSDK/src/kotlin/de/shareui/exterasdk/localization/Strings.kt): localized strings.
  * [`de.shareui.exterasdk.ui.BulletinHelper`](file:///home/shareui/dev/extera/plugins-dev/plugins/sdkkt/KotlinSDK/src/kotlin/de/shareui/exterasdk/ui/BulletinHelper.kt): Telegram bulletin notifications.

## 5. Resources & Localization (`PackIt/res/`)
* `PackIt/res/strings/`:
  * [`config.toml`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/res/strings/config.toml): locale mapping table (`en`, `ru`, `de`).
  * [`en.yml`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/res/strings/en.yml): English strings, base UI keys and `plugin_description`.
  * [`ru.yml`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/res/strings/ru.yml): Russian strings (`plugin_description`).
  * [`de.yml`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/PackIt/res/strings/de.yml): German strings (`plugin_description`).
* [`cruel.toml`](file:///home/shareui/dev/extera/plugins-dev/plugins/ktpackit/cruel.toml):
  * `description = "{plugin_description} [shareui/packit-source](https://github.com/shareui/packit-source)"`: dynamic description template resolved per locale during build.

