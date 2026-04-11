package com.timeofmylife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.Accent

@Composable
fun <T> SegmentedSelector(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    showSuffix: ((T) -> Boolean)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Text(
                    "|",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            val isSelected = option == selected
            val textColor =
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            val style = MaterialTheme.typography.labelMedium
            val chipModifier =
                Modifier
                    .clickable { onSelect(option) }
                    .then(
                        if (isSelected) {
                            Modifier.drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                drawLine(
                                    color = Accent,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = strokeWidth,
                                )
                            }
                        } else {
                            Modifier
                        },
                    )
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            val text = label(option)
            if (showSuffix != null) {
                val visible = showSuffix(option)
                // Find where the suffix starts (last space-separated token with arrow chars)
                val spaceIdx = text.lastIndexOf(' ')
                val base = if (spaceIdx >= 0) text.substring(0, spaceIdx) else text
                val suffix = if (spaceIdx >= 0) text.substring(spaceIdx) else ""
                Row(modifier = chipModifier) {
                    Text(text = base, style = style, color = textColor)
                    Text(
                        text = suffix,
                        style = style,
                        color = textColor,
                        modifier = Modifier.alpha(if (visible) 1f else 0f),
                    )
                }
            } else {
                Text(text = text, style = style, color = textColor, modifier = chipModifier)
            }
        }
    }
}
