# ktpackit Structure

## 1. Kotlin (`PackIt/src/kotlin/`)

### Core (`sh/packit/core/` -> `Core.dex`)
- `PackIt/src/kotlin/sh/packit/core/stickers/TelegramStickerLoader.kt` - sticker loading, caching, placeholder
- `PackIt/src/kotlin/sh/packit/core/info/ClientInfo.kt` - client fork detection
- `PackIt/src/kotlin/sh/packit/core/bridge/NavigationBridge.kt` - URL navigation constants and bridge
- `PackIt/src/kotlin/sh/packit/core/utils/OpenUrl.kt` - in-app and browser URL openers
- `PackIt/src/kotlin/sh/packit/core/utils/Logx.kt` - unified logger with FileLog fallback
- `PackIt/src/kotlin/sh/packit/core/state/CoreState.kt` - state flags and plugin version resolution
- `PackIt/src/kotlin/sh/packit/core/ui/RestartRequired.kt` - restart bulletin notification

### Compose UI (`sh/packit/compose/` -> `Compose.dex`)
- `PackIt/src/kotlin/sh/packit/compose/ComposeEntry.kt` - ComposeFragment entrypoint, routes between screens
- `PackIt/src/kotlin/sh/packit/compose/activities/MainActivity.kt` - main settings screen
- `PackIt/src/kotlin/sh/packit/compose/activities/Settings.kt` - sub-fragment settings screen and SettingsActivity launcher
- `PackIt/src/kotlin/sh/packit/compose/activities/DebugActivity.kt` - debug menu activity and screen (debug_logs switch)
- `PackIt/src/kotlin/sh/packit/compose/activities/AppearanceActivity.kt` - appearance screen (font selector, size slider, reset)
- `PackIt/src/kotlin/sh/packit/compose/components/` - UI components (stickers, items, switches with M3/PixelPlayer contrast, selectors, sliders, headers, footers, groups, bounce animations)
- `PackIt/src/kotlin/sh/packit/compose/utils/` - Compose utilities (drawables, FontHelper)

### Compile-time Stubs (`PackIt/src/kotlin/stubs/`)
- `PackIt/src/kotlin/stubs/org/telegram/` - Telegram client APIs
- `PackIt/src/kotlin/stubs/de/shareui/composeshell/` - Compose theme bridge (TelegramTheme with M3 tonal contrast, TelegramColors)
- `PackIt/src/kotlin/stubs/de/shareui/exterasdk/` - KotlinSDK stubs (Metadata, Strings, BulletinHelper, AndroidUtils, PluginSettings)

## 2. Python (`PackIt/src/python/`)
- `PackIt/src/python/BasePlugin.py` - plugin lifecycle, open_settings, open_sub_settings
- `PackIt/src/python/Main.py` - background init and startup checks
- `PackIt/src/python/core/DexLoader.py` - Core.dex loader and cross-dex reflection utilities
- `PackIt/src/python/ui/activities/PluginSettings.py` - opens ComposeFragment with CoreLoader chaining

## 3. Build & Tools
- `tools/compile/` - Gradle project compiling Compose Kotlin sources to DEX
- `cruel/builds/hooks/compile_core.py` - compiles Core via kotlinc + d8 -> Core.dex
- `cruel/builds/hooks/compile_compose.py` - builds Compose via Gradle dexBuilderRelease and D8 merge -> Compose.dex
- `cruel/local/cache/kotlin/` - build and toolchain cache
- `PackIt/res/assets/` - destination for packed DEX files (cleaned after pack)

## 4. KotlinSDK & ComposeShell Integration
- `de.shareui.exterasdk.metadata.Metadata` - plugin version and metadata
- `de.shareui.exterasdk.localization.Strings` - localized strings
- `de.shareui.exterasdk.ui.BulletinHelper` - Telegram bulletins
- `de.shareui.exterasdk.utils.AndroidUtils` - UI/queue dispatch and static log
- `de.shareui.exterasdk.settings.PluginSettings` - persistent key-value plugin settings
- `de.shareui.exterasdk.settings.ComposeShell` - ComposeFragment launcher with parentLoader support
- Chaquopy Interop:
  - Python modules are resolved under `ElyxPlugins.<plugin_id>.*`
  - Method reflection on `PyObject.callAttr` uses `*arrayOf(method, args)`

## 5. Resources & Localization (`PackIt/res/`)
- `PackIt/res/strings/config.toml` - language mapping table
- `PackIt/res/strings/en.yml`, `ru.yml`, `de.yml` - localization strings
- `cruel.toml` - plugin metadata and build pipeline configuration
