package sh.packit.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.shareui.composeshell.TelegramColors

@Composable
fun SettingsHeader(
    stickerKey: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onStickerLongClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TelegramSticker(
            stickerKey = stickerKey,
            sizeDp = 110.dp,
            roundRadiusDp = 36.dp,
            onLongClick = onStickerLongClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
