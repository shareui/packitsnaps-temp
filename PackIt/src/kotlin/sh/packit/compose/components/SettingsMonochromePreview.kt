package sh.packit.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import sh.packit.compose.utils.LocalMonochromeIcons

@Composable
fun SettingsMonochromePreview(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    isMonochrome: Boolean = LocalMonochromeIcons.current
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MonochromePreviewRow(
            categoryKey = "plugins",
            titleWidth = 72.dp,
            subtitleWidth = 44.dp,
            isDark = isDark,
            isMonochrome = isMonochrome
        )
        MonochromePreviewRow(
            categoryKey = "icons",
            titleWidth = 86.dp,
            subtitleWidth = 52.dp,
            isDark = isDark,
            isMonochrome = isMonochrome
        )
        MonochromePreviewRow(
            categoryKey = "updates",
            titleWidth = 62.dp,
            subtitleWidth = 36.dp,
            isDark = isDark,
            isMonochrome = isMonochrome
        )
    }
}

@Composable
private fun MonochromePreviewRow(
    categoryKey: String,
    titleWidth: Dp,
    subtitleWidth: Dp,
    isDark: Boolean,
    isMonochrome: Boolean
) {
    val rawColors: Pair<Color, Color> = ExpressivePalette.categoryColors(categoryKey, isDark, isMonochrome)
    val containerColor: Color by animateColorAsState(
        targetValue = rawColors.first,
        animationSpec = tween(durationMillis = 250),
        label = "previewContainerColor"
    )
    val tintColor: Color by animateColorAsState(
        targetValue = rawColors.second,
        animationSpec = tween(durationMillis = 250),
        label = "previewTintColor"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonochromePreviewDot(containerColor = containerColor, tintColor = tintColor)
        Spacer(modifier = Modifier.width(10.dp))
        MonochromePreviewText(titleWidth = titleWidth, subtitleWidth = subtitleWidth)
        Spacer(modifier = Modifier.weight(1f))
        MonochromePreviewTrailing()
    }
}

@Composable
private fun MonochromePreviewDot(
    containerColor: Color,
    tintColor: Color
) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(tintColor)
        )
    }
}

@Composable
private fun MonochromePreviewText(
    titleWidth: Dp,
    subtitleWidth: Dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(width = titleWidth, height = 6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .size(width = subtitleWidth, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.20f))
        )
    }
}

@Composable
private fun MonochromePreviewTrailing() {
    Box(
        modifier = Modifier
            .size(width = 10.dp, height = 4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.15f))
    )
}
