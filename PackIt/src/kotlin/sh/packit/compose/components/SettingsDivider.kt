package sh.packit.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors

@Composable
fun SettingsDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 76.dp
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startIndent),
        thickness = 0.5.dp,
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_DIVIDER.copy(alpha = 0.4f)
    )
}
