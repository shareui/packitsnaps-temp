package sh.packit.compose.activities

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.composeshell.TelegramThemeBridge
import de.shareui.exterasdk.localization.Strings
import de.shareui.exterasdk.ui.BulletinHelper
import sh.packit.compose.components.ExpressivePalette
import sh.packit.compose.components.ExpressiveSettingsGroup
import sh.packit.compose.components.SettingsFooter
import sh.packit.compose.components.SettingsHeader
import sh.packit.compose.components.SettingsItem
import sh.packit.compose.components.SettingsSectionTitle
import sh.packit.compose.components.expressiveShapeFor
import sh.packit.compose.utils.ScrollHelper
import sh.packit.compose.utils.rememberScreenScrollState
import sh.packit.core.bridge.NavigationBridge
import sh.packit.core.state.CoreState
import sh.packit.core.utils.OpenUrl

@Composable
fun MainActivityScreen(
    modifier: Modifier = Modifier,
    onAction: ((String) -> Unit)? = null
) {
    val strings: Strings = Strings.of(CoreState.PLUGIN_ID)
    val scrollState: ScrollState = rememberScreenScrollState("main")
    val isDark: Boolean = TelegramThemeBridge.isDark || isSystemInDarkTheme()
    val context: Context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            SettingsHeader(
                stickerKey = "plugin232/17",
                title = "PackIt",
                subtitle = strings.get("plugin_subtitle", "Resource catalog for exteraGram"),
                onStickerLongClick = {
                    ScrollHelper.onNavigateForward("main")
                    if (onAction != null) {
                        onAction("debug_menu")
                    } else {
                        DebugActivity.open(context, strings.get("debug_menu", "Debug menu"))
                    }
                }
            )

            CategoriesSection(strings = strings, isDark = isDark, onAction = onAction)
            Spacer(modifier = Modifier.height(6.dp))
            PreferencesSection(strings = strings, isDark = isDark, onAction = onAction)
            Spacer(modifier = Modifier.height(6.dp))
            CommunitySection(strings = strings, isDark = isDark, onAction = onAction)

            SettingsFooter()
        }
    }
}

@Composable
fun MainSettingsScreen(
    modifier: Modifier = Modifier,
    onAction: ((String) -> Unit)? = null
) {
    MainActivityScreen(modifier = modifier, onAction = onAction)
}

@Composable
private fun CategoriesSection(
    strings: Strings,
    isDark: Boolean,
    onAction: ((String) -> Unit)?
) {
    SettingsSectionTitle(title = strings.get("plugins_header", "Categories"))
    ExpressiveSettingsGroup {
        SettingsItem(
            title = strings.get("install_plugin", "Plugin Catalog"),
            subtitle = strings.get("install_plugin_sub", "Download extensions"),
            iconName = "msg_download",
            iconColors = ExpressivePalette.categoryColors("plugins", isDark),
            shape = expressiveShapeFor(0, 3),
            onClick = { onAction?.invoke("install_plugins") ?: BulletinHelper.showInfo("Coming soon") }
        )
        SettingsItem(
            title = strings.get("install_icons", "Icon Catalog"),
            subtitle = strings.get("install_icons_sub", "Custom icon packs"),
            iconName = "msg_smile_status",
            iconColors = ExpressivePalette.categoryColors("icons", isDark),
            shape = expressiveShapeFor(1, 3),
            onClick = { onAction?.invoke("install_icons") ?: BulletinHelper.showInfo("Coming soon") }
        )
        SettingsItem(
            title = strings.get("check_updates", "Check for Updates"),
            subtitle = strings.get("check_updates_sub", "Check for plugins updates"),
            iconName = "msg_retry",
            iconColors = ExpressivePalette.categoryColors("updates", isDark),
            shape = expressiveShapeFor(2, 3),
            onClick = { onAction?.invoke("check_updates") ?: BulletinHelper.showInfo("Checking updates...") }
        )
    }
}

@Composable
private fun PreferencesSection(
    strings: Strings,
    isDark: Boolean,
    onAction: ((String) -> Unit)?
) {
    val context = LocalContext.current

    SettingsSectionTitle(title = strings.get("settings_header", "Preferences"))
    ExpressiveSettingsGroup {
        SettingsItem(
            title = strings.get("deeplinks", "Deeplinks"),
            iconName = "msg_link",
            iconColors = ExpressivePalette.categoryColors("deeplinks", isDark),
            shape = expressiveShapeFor(0, 5),
            onClick = { onAction?.invoke("deeplinks") ?: BulletinHelper.showInfo("Deeplinks") }
        )
        SettingsItem(
            title = strings.get("repositories", "Repositories"),
            iconName = "msg_folders",
            iconColors = ExpressivePalette.categoryColors("repositories", isDark),
            shape = expressiveShapeFor(1, 5),
            onClick = { onAction?.invoke("repositories") ?: BulletinHelper.showInfo("Repositories") }
        )
        SettingsItem(
            title = strings.get("profile", "Profile"),
            iconName = "msg_contacts",
            iconColors = ExpressivePalette.categoryColors("profile", isDark),
            shape = expressiveShapeFor(2, 5),
            onClick = { onAction?.invoke("profile") ?: BulletinHelper.showInfo("Profile") }
        )
        SettingsItem(
            title = strings.get("utilities", "Utilities"),
            iconName = "msg_work",
            iconColors = ExpressivePalette.categoryColors("utilities", isDark),
            shape = expressiveShapeFor(3, 5),
            onClick = { onAction?.invoke("utilities") ?: BulletinHelper.showInfo("Utilities") }
        )
        SettingsItem(
            title = strings.get("other_settings", "Settings"),
            iconName = "msg_settings",
            iconColors = ExpressivePalette.categoryColors("settings", isDark),
            shape = expressiveShapeFor(4, 5),
            onClick = {
                ScrollHelper.onNavigateForward("main")
                if (onAction != null) {
                    onAction("other_settings")
                } else {
                    SettingsActivity.open(context, strings.get("other_settings", "Settings"))
                }
            }
        )
    }
}

@Composable
private fun CommunitySection(
    strings: Strings,
    isDark: Boolean,
    onAction: ((String) -> Unit)?
) {
    val context = LocalContext.current
    val colors = ExpressivePalette.categoryColors("community", isDark)

    SettingsSectionTitle(title = strings.get("community_header", "Community"))
    ExpressiveSettingsGroup {
        SettingsItem(
            title = strings.get("packit_channel", "PackIt Channel"),
            iconName = "msg_channel",
            iconColors = colors,
            shape = expressiveShapeFor(0, 5),
            onClick = { OpenUrl.openUrlInApp(context, NavigationBridge.URL_CHANNEL) }
        )
        SettingsItem(
            title = strings.get("packit_forum", "Packit Forum"),
            iconName = "msg_groups",
            iconColors = colors,
            shape = expressiveShapeFor(1, 5),
            onClick = { OpenUrl.openUrlInApp(context, NavigationBridge.URL_FORUM) }
        )
        SettingsItem(
            title = strings.get("source_code", "Source code"),
            iconName = "msg_link",
            iconColors = colors,
            shape = expressiveShapeFor(2, 5),
            onClick = { OpenUrl.openUrlInBrowser(context, NavigationBridge.URL_SOURCE) }
        )
        SettingsItem(
            title = strings.get("links_docs", "Links & Documentation"),
            iconName = "msg_help",
            iconColors = colors,
            shape = expressiveShapeFor(3, 5),
            onClick = { onAction?.invoke("docs") ?: BulletinHelper.showInfo("Documentation") }
        )
        SettingsItem(
            title = strings.get("contributors", "Contributors"),
            iconName = "msg_contacts",
            iconColors = colors,
            shape = expressiveShapeFor(4, 5),
            onClick = { onAction?.invoke("contributors") ?: BulletinHelper.showInfo("Contributors") }
        )
    }
}
