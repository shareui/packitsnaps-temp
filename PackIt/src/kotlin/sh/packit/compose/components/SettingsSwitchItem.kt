package sh.packit.compose.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.compose.utils.LocalMonochromeIcons
import sh.packit.compose.utils.rememberTelegramPainter
import sh.packit.core.state.CoreState

@Composable
fun SettingsSwitchItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    settingKey: String? = null,
    pluginId: String = CoreState.PLUGIN_ID,
    checked: Boolean? = null,
    default: Boolean = false,
    enabled: Boolean = true,
    imageVector: ImageVector? = null,
    iconName: String? = null,
    iconRes: Int? = null,
    iconColors: Pair<Color, Color> = TelegramColors.DEFAULT_PLUGINSETTINGS_BG to TelegramColors.windowBackgroundWhiteBlueIcon,
    shape: Shape = RoundedCornerShape(24.dp),
    onSwitch: ((Boolean) -> Unit)? = null
) {
    val isMonochrome: Boolean = LocalMonochromeIcons.current
    val effectiveColors: Pair<Color, Color> = if (isMonochrome) ExpressivePalette.monochromeColors() else iconColors

    val initialValue: Boolean = remember(settingKey, pluginId, default) {
        if (!settingKey.isNullOrEmpty()) {
            PluginSettings.getSetting(pluginId, settingKey, default)
        } else {
            default
        }
    }
    var internalChecked: Boolean by remember(settingKey, pluginId) { mutableStateOf(initialValue) }
    val currentChecked: Boolean = checked ?: internalChecked

    val handleToggle: (Boolean) -> Unit = { newValue ->
        if (enabled) {
            if (checked == null) internalChecked = newValue
            if (!settingKey.isNullOrEmpty()) {
                PluginSettings.setSetting(pluginId, settingKey, newValue, reloadSettings = true)
            }
            onSwitch?.invoke(newValue)
        }
    }

    val iconPainter: Painter? = when {
        imageVector != null -> rememberVectorPainter(imageVector)
        iconName != null -> rememberTelegramPainter(iconName)
        iconRes != null && iconRes != 0 -> rememberTelegramPainter(iconRes)
        else -> null
    }

    Surface(
        shape = shape,
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .expressiveBounce(
                targetScale = 0.98f,
                enabled = enabled,
                onClick = { handleToggle(!currentChecked) }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPainter != null) {
                ItemLeadingIcon(
                    painter = iconPainter,
                    contentDescription = title,
                    tintColor = effectiveColors.second.takeIf { it != Color.Unspecified }
                        ?: TelegramColors.windowBackgroundWhiteBlueIcon
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            SwitchTextColumn(
                title = title,
                subtitle = subtitle,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            SwitchControl(
                checked = currentChecked,
                enabled = enabled,
                onToggle = handleToggle
            )
        }
    }
}

@Composable
private fun SwitchTextColumn(
    title: String,
    subtitle: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryAlpha: Float = if (enabled) 1.0f else 0.4f
    val secondaryAlpha: Float = if (enabled) 0.7f else 0.4f

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT.copy(alpha = primaryAlpha),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!subtitle.isNullOrEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = secondaryAlpha),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SwitchControl(
    checked: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val activeTrack = MaterialTheme.colorScheme.primary
    val activeThumb = MaterialTheme.colorScheme.onPrimary
    val inactiveTrack = MaterialTheme.colorScheme.surfaceVariant
    val inactiveThumb = MaterialTheme.colorScheme.onSurfaceVariant

    Switch(
        checked = checked,
        onCheckedChange = onToggle,
        enabled = enabled,
        thumbContent = {
            AnimatedContent(
                targetState = checked,
                transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
                label = "switch_thumb_icon"
            ) { isChecked ->
                Icon(
                    imageVector = if (isChecked) SwitchCheckIcon else SwitchCloseIcon,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = if (isChecked) activeTrack else inactiveTrack
                )
            }
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = activeThumb,
            checkedTrackColor = activeTrack,
            checkedIconColor = activeTrack,
            uncheckedThumbColor = inactiveThumb,
            uncheckedTrackColor = inactiveTrack,
            uncheckedIconColor = inactiveTrack,
            uncheckedBorderColor = Color.Transparent,
            checkedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun ItemLeadingIcon(
    painter: Painter,
    contentDescription: String,
    tintColor: Color
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tintColor
        )
    }
}

private val SwitchCheckIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "SwitchCheck",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.White)) {
        moveTo(9.0f, 16.17f)
        lineTo(4.83f, 12.0f)
        lineTo(3.41f, 13.41f)
        lineTo(9.0f, 19.0f)
        lineTo(21.0f, 7.0f)
        lineTo(19.59f, 5.59f)
        close()
    }.build()
}

private val SwitchCloseIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "SwitchClose",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.White)) {
        moveTo(19.0f, 6.41f)
        lineTo(17.59f, 5.0f)
        lineTo(12.0f, 10.59f)
        lineTo(6.41f, 5.0f)
        lineTo(5.0f, 6.41f)
        lineTo(10.59f, 12.0f)
        lineTo(5.0f, 17.59f)
        lineTo(6.41f, 19.0f)
        lineTo(12.0f, 13.41f)
        lineTo(17.59f, 19.0f)
        lineTo(19.0f, 17.59f)
        lineTo(13.41f, 12.0f)
        close()
    }.build()
}

