package sh.packit.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.shareui.composeshell.TelegramColors
import sh.packit.compose.utils.rememberTelegramPainter

@Composable
fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconName: String? = null,
    iconRes: Int? = null,
    iconTint: Color = TelegramColors.windowBackgroundWhiteBlueIcon,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    val iconPainter = if (iconName != null) {
        rememberTelegramPainter(iconName)
    } else if (iconRes != null && iconRes != 0) {
        rememberTelegramPainter(iconRes)
    } else {
        null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(clickModifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null && subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (showChevron) {
            val chevronPainter = rememberTelegramPainter("msg_arrowright")
            if (chevronPainter != null) {
                Icon(
                    painter = chevronPainter,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.5f)
                )
            }
        }
    }
}
