package sh.packit.compose.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SettingsFooter(
    clientLabel: String,
    modifier: Modifier = Modifier
) {
    val chipShape = RoundedCornerShape(12.dp)
    val strokeColor = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.copy(alpha = 0.3f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = clientLabel,
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .border(width = 1.dp, color = strokeColor, shape = chipShape)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Powered by ElyxCore",
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
