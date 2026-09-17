package sh.packit.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.compose.icons.size20dp.check
import sh.packit.compose.utils.LocalMonochromeIcons
import sh.packit.compose.utils.rememberTelegramPainter
import sh.packit.core.state.CoreState

data class SelectorSubOption(
    val key: String,
    val label: String,
    val font: FontFamily? = null
)

data class SelectorOption(
    val key: String,
    val label: String,
    val font: FontFamily? = null,
    val expandable: Boolean = false,
    val subOptions: List<SelectorSubOption> = emptyList()
)

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
    selectorOptions: List<SelectorOption>? = null,
    imageVector: ImageVector? = null,
    iconName: String? = null,
    iconRes: Int? = null,
    iconColors: Pair<Color, Color> = TelegramColors.DEFAULT_PLUGINSETTINGS_BG to TelegramColors.windowBackgroundWhiteBlueIcon,
    shape: Shape = RoundedCornerShape(24.dp),
    onSelectionChanged: ((String) -> Unit)? = null
) {
    val isMonochrome: Boolean = LocalMonochromeIcons.current
    val effectiveColors: Pair<Color, Color> = if (isMonochrome) ExpressivePalette.monochromeColors() else iconColors

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

    val effectiveOptions: List<SelectorOption> = remember(selectorOptions, options, optionsFonts) {
        selectorOptions ?: options.map { (k, v) ->
            SelectorOption(
                key = k,
                label = v,
                font = optionsFonts?.get(k),
                expandable = false,
                subOptions = emptyList()
            )
        }
    }

    val (resolvedLabel, resolvedFont) = remember(currentKey, effectiveOptions) {
        for (opt in effectiveOptions) {
            if (opt.key == currentKey) {
                return@remember opt.label to opt.font
            }
            for (sub in opt.subOptions) {
                if (sub.key == currentKey) {
                    return@remember "${opt.label} ${sub.label}" to (sub.font ?: opt.font)
                }
            }
        }
        (options[currentKey] ?: currentKey) to optionsFonts?.get(currentKey)
    }

    val handleSelection: (String) -> Unit = { newKey ->
        if (selectedKey == null) internalKey = newKey
        if (!settingKey.isNullOrEmpty()) {
            PluginSettings.setSetting(pluginId, settingKey, newKey, reloadSettings = true)
        }
        onSelectionChanged?.invoke(newKey)
    }

    val iconPainter: Painter? = when {
        imageVector != null -> rememberVectorPainter(imageVector)
        iconName != null -> rememberTelegramPainter(iconName)
        iconRes != null && iconRes != 0 -> rememberTelegramPainter(iconRes)
        else -> null
    }

    Surface(
        shape = shape,
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .expressiveBounce(targetScale = 0.98f, onClick = { showSheet = true })
    ) {
        SelectorItemContent(
            title = title,
            subtitle = subtitle,
            selectedLabel = resolvedLabel,
            selectedFont = resolvedFont,
            iconPainter = iconPainter,
            iconColors = effectiveColors
        )
    }

    if (showSheet) {
        SelectorBottomSheet(
            title = title,
            options = effectiveOptions,
            currentKey = currentKey,
            defaultKey = defaultKey,
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
            SelectorTitleRow(
                title = title,
                selectedLabel = selectedLabel,
                selectedFont = selectedFont
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
        }
    }
}

@Composable
private fun SelectorTitleRow(
    title: String,
    selectedLabel: String,
    selectedFont: FontFamily?
) {
    val borderColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_DIVIDER.takeIf { it != Color.Unspecified }
        ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp)
        )
        Surface(
            color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
            shape = CircleShape,
            border = BorderStroke(width = 1.dp, color = borderColor),
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(start = 8.dp)
        ) {
            Text(
                text = selectedLabel,
                style = MaterialTheme.typography.labelMedium,
                color = TelegramColors.windowBackgroundWhiteBlueText,
                fontWeight = FontWeight.Bold,
                fontFamily = selectedFont ?: FontFamily.Default,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorBottomSheet(
    title: String,
    options: List<SelectorOption>,
    currentKey: String,
    defaultKey: String = "",
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var expandedKey: String? by remember { mutableStateOf(null) }
    val scrollState = rememberScrollState()
    val hasExpandable: Boolean = remember(options) { options.any { it.expandable } }

    LaunchedEffect(expandedKey) {
        if (expandedKey != null) scrollState.animateScrollTo(0)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
        contentColor = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .animateContentSize(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 440.dp)
                    .verticalScroll(scrollState)
            ) {
                options.forEach { option ->
                    val isDefault: Boolean = option.key == defaultKey ||
                            option.key.equals("default", ignoreCase = true) ||
                            (!option.expandable && hasExpandable)
                    val isExpanded: Boolean = expandedKey == option.key
                    val shouldBeVisible: Boolean = expandedKey == null || isDefault || isExpanded

                    ExpandableOptionItem(
                        option = option,
                        currentKey = currentKey,
                        isExpanded = isExpanded,
                        shouldBeVisible = shouldBeVisible,
                        onToggleExpand = { expandedKey = if (isExpanded) null else option.key },
                        onSelect = onSelect,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableOptionItem(
    option: SelectorOption,
    currentKey: String,
    isExpanded: Boolean,
    shouldBeVisible: Boolean,
    onToggleExpand: () -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isOptionActive: Boolean = option.key == currentKey || option.subOptions.any { it.key == currentKey }
    val isExpandable: Boolean = option.expandable && option.subOptions.isNotEmpty()
    val animProgress: Float by animateFloatAsState(
        targetValue = if (shouldBeVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "optionAnimProgress"
    )

    AnimatedVisibility(
        visible = shouldBeVisible,
        enter = fadeIn(tween(220, easing = LinearOutSlowInEasing)) +
                expandVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
                    expandFrom = Alignment.Top
                ) +
                scaleIn(initialScale = 0.94f, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)),
        exit = fadeOut(tween(160, easing = FastOutLinearInEasing)) +
                shrinkVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
                    shrinkTowards = Alignment.Top
                ) +
                scaleOut(targetScale = 0.94f, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .blur(((1f - animProgress) * 10f).dp)
        ) {
            SelectorOptionRow(
                label = option.label,
                isActive = isOptionActive,
                isExpandable = isExpandable,
                isExpanded = isExpanded,
                font = option.font,
                onClick = { if (isExpandable) onToggleExpand() else { onSelect(option.key); onDismiss() } }
            )
            AnimatedVisibility(
                visible = isExpandable && isExpanded,
                enter = fadeIn(tween(180, easing = LinearOutSlowInEasing)) +
                        expandVertically(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow), expandFrom = Alignment.Top),
                exit = fadeOut(tween(140, easing = FastOutLinearInEasing)) +
                        shrinkVertically(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow), shrinkTowards = Alignment.Top)
            ) {
                SubOptionsList(subOptions = option.subOptions, currentKey = currentKey, onSelect = onSelect, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun SubOptionsList(
    subOptions: List<SelectorSubOption>,
    currentKey: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        subOptions.forEach { subOption ->
            SelectorSubOptionRow(
                label = subOption.label,
                isActive = subOption.key == currentKey,
                font = subOption.font,
                onClick = {
                    onSelect(subOption.key)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun SelectorOptionRow(
    label: String,
    isActive: Boolean,
    isExpandable: Boolean,
    isExpanded: Boolean,
    font: FontFamily?,
    onClick: () -> Unit
) {
    val bgColor: Color = if (isActive) {
        TelegramColors.windowBackgroundWhiteBlueText.copy(alpha = 0.15f)
    } else {
        TelegramColors.DEFAULT_PLUGINSETTINGS_BG
    }
    val contentColor: Color = if (isActive) {
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
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontFamily = font ?: FontFamily.Default,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            if (isExpandable) {
                MorphingArrowIcon(
                    isExpanded = isExpanded,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isActive) {
                Icon(
                    imageVector = check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectorSubOptionRow(
    label: String,
    isActive: Boolean,
    font: FontFamily?,
    onClick: () -> Unit
) {
    val bgColor: Color = if (isActive) {
        TelegramColors.windowBackgroundWhiteBlueText.copy(alpha = 0.15f)
    } else {
        TelegramColors.DEFAULT_PLUGINSETTINGS_BG.copy(alpha = 0.7f)
    }
    val contentColor: Color = if (isActive) {
        TelegramColors.windowBackgroundWhiteBlueText
    } else {
        TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            color = bgColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = font ?: FontFamily.Default,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                if (isActive) {
                    Icon(
                        imageVector = check,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
