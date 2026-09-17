package sh.packit.compose

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import de.shareui.composeshell.TelegramTheme
import de.shareui.exterasdk.localization.Strings
import de.shareui.exterasdk.ui.BulletinHelper
import org.json.JSONObject
import sh.packit.compose.utils.PackItTheme
import sh.packit.compose.activities.AppearanceActivity
import sh.packit.compose.activities.AppearanceScreen
import sh.packit.compose.activities.DebugActivity
import sh.packit.compose.activities.DebugScreen
import sh.packit.compose.activities.MainActivityScreen
import sh.packit.compose.activities.SettingsActivity
import sh.packit.compose.activities.SettingsScreen
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Logx

object ComposeEntry {
    @JvmStatic
    fun createView(context: Context, argsJson: String?): View {
        Logx.logx("ComposeEntry.createView started", isDebug = true)
        CoreState.markComposeReady()

        var assetsDir: String? = null
        val screen: String = try {
            if (argsJson != null) {
                val json = JSONObject(argsJson)
                val dir = json.optString("assets_dir", null)
                if (!dir.isNullOrEmpty()) {
                    CoreState.assetsDir = dir
                    assetsDir = dir
                }
                json.optString("screen", "main")
            } else "main"
        } catch (_: Throwable) {
            "main"
        }

        return ComposeView(context).apply {
            setContent {
                PackItTheme(assetsDir = assetsDir) {
                    when (screen) {
                        "settings" -> SettingsScreen()
                        "debug" -> DebugScreen()
                        "appearance" -> AppearanceScreen(assetsDir = assetsDir)
                        else -> MainActivityScreen(
                            onAction = { action ->
                                val strings: Strings = Strings.of(CoreState.PLUGIN_ID)
                                when (action) {
                                    "appearance" -> {
                                        sh.packit.compose.utils.ScrollHelper.onNavigateForward("main")
                                        val title: String = strings.get("appearance", "Appearance")
                                        val opened: Boolean = AppearanceActivity.open(context, title)
                                        if (!opened) {
                                            BulletinHelper.showError("Failed to open Appearance")
                                        }
                                    }
                                    "debug_menu", "debug" -> {
                                        sh.packit.compose.utils.ScrollHelper.onNavigateForward("main")
                                        val title: String = strings.get("debug_menu", "Debug menu")
                                        val opened: Boolean = DebugActivity.open(context, title)
                                        if (!opened) {
                                            BulletinHelper.showError("Failed to open Debug")
                                        }
                                    }
                                    "other_settings", "settings" -> {
                                        sh.packit.compose.utils.ScrollHelper.onNavigateForward("main")
                                        val title: String = strings.get("other_settings", "Settings")
                                        val opened: Boolean = SettingsActivity.open(context, title)
                                        if (!opened) {
                                            BulletinHelper.showError("Failed to open Settings")
                                        }
                                    }
                                    "install_plugins" -> BulletinHelper.showInfo(strings.get("install_plugin", "Plugin Catalog"))
                                    "install_icons" -> BulletinHelper.showInfo(strings.get("install_icons", "Icon Catalog"))
                                    "check_updates" -> BulletinHelper.showInfo(strings.get("check_updates", "Check for Updates"))
                                    "deeplinks" -> BulletinHelper.showInfo(strings.get("deeplinks", "Deeplinks"))
                                    "repositories" -> BulletinHelper.showInfo(strings.get("repositories", "Repositories"))
                                    "profile" -> BulletinHelper.showInfo(strings.get("profile", "Profile"))
                                    "utilities" -> BulletinHelper.showInfo(strings.get("utilities", "Utilities"))
                                    "docs" -> BulletinHelper.showInfo(strings.get("links_docs", "Links & Documentation"))
                                    "contributors" -> BulletinHelper.showInfo(strings.get("contributors", "Contributors"))
                                    else -> Logx.logx("unhandled action: $action", isDebug = true)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

