package sh.packit.compose.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    iconColors: Pair<Color, Color> = TelegramColors.DEFAULT_PLUGINSETTINGS_BG to TelegramColors.windowBackgroundWhiteBlueIcon,
    shape: Shape = RoundedCornerShape(24.dp),
    badgeText: String? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
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
            .expressiveBounce(targetScale = 0.97f, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPainter != null) {
                ItemIconBadge(
                    painter = iconPainter,
                    contentDescription = title,
                    containerColor = iconColors.first,
                    tintColor = iconColors.second
                )
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
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (!badgeText.isNullOrEmpty()) {
                ItemBadge(text = badgeText, colors = iconColors)
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showChevron) {
                ItemChevron()
            }
        }
    }
}

@Composable
private fun ItemIconBadge(
    painter: Painter,
    contentDescription: String,
    containerColor: Color,
    tintColor: Color
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(containerColor),
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

@Composable
private fun ItemBadge(
    text: String,
    colors: Pair<Color, Color>
) {
    TextCloud(
        text = text,
        containerColor = colors.first,
        contentColor = colors.second,
        horizontalPadding = 10.dp,
        verticalPadding = 4.dp
    )
}

@Composable
private fun ItemChevron() {
    val chevronPainter: Painter? = rememberTelegramPainter("msg_arrowright")
    if (chevronPainter != null) {
        Icon(
            painter = chevronPainter,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.35f)
        )
    }
}
