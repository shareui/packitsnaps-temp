package sh.packit.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.compose.utils.rememberTelegramPainter
import sh.packit.core.state.CoreState

@Composable
fun SettingsDottedSlider(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    settingKey: String? = null,
    pluginId: String = CoreState.PLUGIN_ID,
    value: Float? = null,
    defaultValue: Float = 16f,
    valueRange: ClosedFloatingPointRange<Float> = 8f..24f,
    steps: Int = 15,
    iconName: String? = null,
    iconRes: Int? = null,
    iconColors: Pair<Color, Color> = TelegramColors.DEFAULT_PLUGINSETTINGS_BG to TelegramColors.windowBackgroundWhiteBlueIcon,
    shape: Shape = RoundedCornerShape(24.dp),
    valueText: (Float) -> String = { "${it.toInt()}px" },
    onValueChange: ((Float) -> Unit)? = null,
    onValueChangeFinished: ((Float) -> Unit)? = null
) {
    val initialValue: Float = remember(settingKey, pluginId, defaultValue) {
        if (!settingKey.isNullOrEmpty()) {
            val raw: Any? = PluginSettings.getSetting(pluginId, settingKey, defaultValue)
            when (raw) {
                is Number -> raw.toFloat()
                is String -> raw.toFloatOrNull() ?: defaultValue
                else -> defaultValue
            }
        } else {
            defaultValue
        }
    }
    var internalValue: Float by remember(settingKey, pluginId) { mutableFloatStateOf(initialValue) }
    val currentValue: Float = value ?: internalValue

    val iconPainter: Painter? = when {
        iconName != null -> rememberTelegramPainter(iconName)
        iconRes != null && iconRes != 0 -> rememberTelegramPainter(iconRes)
        else -> null
    }

    Surface(
        shape = shape,
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            SliderHeader(
                title = title,
                subtitle = subtitle,
                valueText = valueText(currentValue),
                iconPainter = iconPainter,
                iconColors = iconColors
            )
            Spacer(modifier = Modifier.height(10.dp))
            SliderControl(
                value = currentValue,
                valueRange = valueRange,
                steps = steps,
                onValueChange = { newValue ->
                    if (value == null) internalValue = newValue
                    if (!settingKey.isNullOrEmpty()) {
                        PluginSettings.setSetting(pluginId, settingKey, newValue, reloadSettings = true)
                    }
                    onValueChange?.invoke(newValue)
                },
                onValueChangeFinished = {
                    if (!settingKey.isNullOrEmpty()) {
                        PluginSettings.setSetting(pluginId, settingKey, currentValue, reloadSettings = true)
                    }
                    onValueChangeFinished?.invoke(currentValue)
                }
            )
        }
    }
}

@Composable
private fun SliderHeader(
    title: String,
    subtitle: String?,
    valueText: String,
    iconPainter: Painter?,
    iconColors: Pair<Color, Color>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconPainter != null) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = iconPainter,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconColors.second
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TelegramColors.windowBackgroundWhiteBlueText
                )
            }
            if (!subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SliderControl(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = TelegramColors.windowBackgroundWhiteBlueText,
            activeTrackColor = TelegramColors.windowBackgroundWhiteBlueText,
            inactiveTrackColor = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
            activeTickColor = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
            inactiveTickColor = TelegramColors.windowBackgroundWhiteBlueText.copy(alpha = 0.4f)
        )
    )
}
