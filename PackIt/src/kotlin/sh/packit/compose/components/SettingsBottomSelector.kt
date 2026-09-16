package sh.packit.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.compose.utils.rememberTelegramPainter
import sh.packit.core.state.CoreState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSelector(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    settingKey: String? = null,
    pluginId: String = CoreState.PLUGIN_ID,
    selectedKey: String? = null,
    defaultKey: String = "",
    options: Map<String, String> = emptyMap(),
    optionsFonts: Map<String, FontFamily>? = null,
    iconName: String? = null,
    iconRes: Int? = null,
    iconColors: Pair<Color, Color> = TelegramColors.DEFAULT_PLUGINSETTINGS_BG to TelegramColors.windowBackgroundWhiteBlueIcon,
    shape: Shape = RoundedCornerShape(24.dp),
    onSelectionChanged: ((String) -> Unit)? = null
) {
    val initialKey: String = remember(settingKey, pluginId, defaultKey) {
        if (!settingKey.isNullOrEmpty()) {
            PluginSettings.getSetting(pluginId, settingKey, defaultKey)
        } else {
            defaultKey
        }
    }
    var internalKey: String by remember(settingKey, pluginId) { mutableStateOf(initialKey) }
    val currentKey: String = selectedKey ?: internalKey
    var showSheet: Boolean by remember { mutableStateOf(false) }

    val handleSelection: (String) -> Unit = { newKey ->
        if (selectedKey == null) internalKey = newKey
        if (!settingKey.isNullOrEmpty()) {
            PluginSettings.setSetting(pluginId, settingKey, newKey, reloadSettings = true)
        }
        onSelectionChanged?.invoke(newKey)
    }

    val iconPainter: Painter? = when {
        iconName != null -> rememberTelegramPainter(iconName)
        iconRes != null && iconRes != 0 -> rememberTelegramPainter(iconRes)
        else -> null
    }

    Surface(
        shape = shape,
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .expressiveBounce(targetScale = 0.98f, onClick = { showSheet = true })
    ) {
        SelectorItemContent(
            title = title,
            subtitle = subtitle,
            selectedLabel = options[currentKey] ?: currentKey,
            selectedFont = optionsFonts?.get(currentKey),
            iconPainter = iconPainter,
            iconColors = iconColors
        )
    }

    if (showSheet) {
        SelectorBottomSheet(
            title = title,
            options = options,
            optionsFonts = optionsFonts,
            currentKey = currentKey,
            onSelect = handleSelection,
            onDismiss = { showSheet = false }
        )
    }
}

@Composable
private fun SelectorItemContent(
    title: String,
    subtitle: String?,
    selectedLabel: String,
    selectedFont: FontFamily?,
    iconPainter: Painter?,
    iconColors: Pair<Color, Color>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = selectedLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = TelegramColors.windowBackgroundWhiteBlueText,
                    fontWeight = FontWeight.Bold,
                    fontFamily = selectedFont ?: FontFamily.Default,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorBottomSheet(
    title: String,
    options: Map<String, String>,
    optionsFonts: Map<String, FontFamily>?,
    currentKey: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        contentColor = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT
    ) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options.entries.toList()) { entry ->
                    val isSelected: Boolean = entry.key == currentKey
                    SelectorOptionRow(
                        key = entry.key,
                        label = entry.value,
                        isSelected = isSelected,
                        font = optionsFonts?.get(entry.key),
                        onClick = {
                            onSelect(entry.key)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorOptionRow(
    key: String,
    label: String,
    isSelected: Boolean,
    font: FontFamily?,
    onClick: () -> Unit
) {
    val bgColor: Color = if (isSelected) {
        TelegramColors.windowBackgroundWhiteBlueText.copy(alpha = 0.15f)
    } else {
        TelegramColors.DEFAULT_PLUGINSETTINGS_BG
    }
    val contentColor: Color = if (isSelected) {
        TelegramColors.windowBackgroundWhiteBlueText
    } else {
        TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontFamily = font ?: FontFamily.Default,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = SelectorCheckIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private val SelectorCheckIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "SelectorCheck",
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
