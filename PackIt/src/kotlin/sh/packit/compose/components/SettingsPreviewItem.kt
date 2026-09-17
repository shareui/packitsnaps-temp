package sh.packit.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors

@Composable
fun SettingsPreviewItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    innerShape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
    previewBackgroundColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
    contentPadding: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .clip(innerShape)
                .background(previewBackgroundColor),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
fun SettingsPrevievItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    innerShape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
    previewBackgroundColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
    contentPadding: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    SettingsPreviewItem(
        modifier = modifier,
        shape = shape,
        innerShape = innerShape,
        containerColor = containerColor,
        previewBackgroundColor = previewBackgroundColor,
        contentPadding = contentPadding,
        content = content
    )
}
