package sh.packit.compose.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.exterasdk.localization.Strings
import de.shareui.exterasdk.ui.BulletinHelper
import sh.packit.compose.components.SettingsCard
import sh.packit.compose.components.SettingsDivider
import sh.packit.compose.components.SettingsFooter
import sh.packit.compose.components.SettingsHeader
import sh.packit.compose.components.SettingsItem
import sh.packit.compose.components.SettingsSectionTitle
import sh.packit.core.bridge.NavigationBridge
import sh.packit.core.info.ClientInfo
import sh.packit.core.state.CoreState

@Composable
fun MainSettingsScreen(
    modifier: Modifier = Modifier,
    onAction: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val strings = Strings.of(CoreState.PLUGIN_ID)
    val scrollState = rememberScrollState()

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
                title = "PackIt v${CoreState.pluginVersion}",
                subtitle = strings.get("plugin_subtitle", "Resource catalog for exteraGram"),
                onStickerLongClick = {
                    BulletinHelper.showInfo("PackIt Easter Egg :)")
                }
            )

            CategoriesSection(strings = strings, onAction = onAction)
            PreferencesSection(strings = strings, onAction = onAction)
            CommunitySection(strings = strings, onAction = onAction)

            SettingsFooter(clientLabel = ClientInfo.getClientLabel(context))
        }
    }
}

@Composable
private fun CategoriesSection(
    strings: Strings,
    onAction: ((String) -> Unit)?
) {
    SettingsSectionTitle(title = strings.get("plugins_header", "Categories"))
    SettingsCard {
        SettingsItem(
            title = strings.get("install_plugin", "Plugin Catalog"),
            iconName = "msg_download",
            onClick = { onAction?.invoke("install_plugins") ?: BulletinHelper.showInfo("Coming soon") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("install_icons", "Icon Catalog"),
            iconName = "msg_smile_status",
            onClick = { onAction?.invoke("install_icons") ?: BulletinHelper.showInfo("Coming soon") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("check_updates", "Check for Updates"),
            iconName = "msg_retry",
            onClick = { onAction?.invoke("check_updates") ?: BulletinHelper.showInfo("Checking updates...") }
        )
    }
}

@Composable
private fun PreferencesSection(
    strings: Strings,
    onAction: ((String) -> Unit)?
) {
    SettingsSectionTitle(title = strings.get("settings_header", "Other"))
    SettingsCard {
        SettingsItem(
            title = strings.get("deeplinks", "Deeplinks"),
            iconName = "msg_link",
            onClick = { onAction?.invoke("deeplinks") ?: BulletinHelper.showInfo("Deeplinks") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("repositories", "Repositories"),
            iconName = "msg_folders",
            onClick = { onAction?.invoke("repositories") ?: BulletinHelper.showInfo("Repositories") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("profile", "Profile"),
            iconName = "msg_contacts",
            onClick = { onAction?.invoke("profile") ?: BulletinHelper.showInfo("Profile") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("utilities", "Utilities"),
            iconName = "msg_work",
            onClick = { onAction?.invoke("utilities") ?: BulletinHelper.showInfo("Utilities") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("other_settings", "Settings"),
            iconName = "msg_settings",
            onClick = { onAction?.invoke("other_settings") ?: BulletinHelper.showInfo("Settings") }
        )
    }
}

@Composable
private fun CommunitySection(
    strings: Strings,
    onAction: ((String) -> Unit)?
) {
    val context = LocalContext.current
    SettingsSectionTitle(title = strings.get("community_header", "Community"))
    SettingsCard {
        SettingsItem(
            title = strings.get("packit_channel", "PackIt Channel"),
            iconName = "msg_channel",
            onClick = { NavigationBridge.openUrl(context, NavigationBridge.URL_CHANNEL) }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("packit_forum", "Packit Forum"),
            iconName = "msg_groups",
            onClick = { NavigationBridge.openUrl(context, NavigationBridge.URL_FORUM) }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("source_code", "Source code"),
            iconName = "msg_link",
            onClick = { NavigationBridge.openUrl(context, NavigationBridge.URL_SOURCE) }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("links_docs", "Links & Documentation"),
            iconName = "msg_help",
            onClick = { onAction?.invoke("docs") ?: BulletinHelper.showInfo("Documentation") }
        )
        SettingsDivider()
        SettingsItem(
            title = strings.get("contributors", "Contributors"),
            iconName = "msg_contacts",
            onClick = { onAction?.invoke("contributors") ?: BulletinHelper.showInfo("Contributors") }
        )
    }
}
