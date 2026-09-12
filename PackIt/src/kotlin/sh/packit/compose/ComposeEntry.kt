package sh.packit.compose

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import de.shareui.composeshell.TelegramTheme
import sh.packit.compose.activities.MainSettingsScreen
import sh.packit.core.state.CoreState

object ComposeEntry {
    @JvmStatic
    fun createView(context: Context, argsJson: String?): View {
        CoreState.markComposeReady()

        return ComposeView(context).apply {
            setContent {
                TelegramTheme {
                    MainSettingsScreen()
                }
            }
        }
    }
}
