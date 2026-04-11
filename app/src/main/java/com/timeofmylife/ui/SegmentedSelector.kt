package com.timeofmylife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.Accent

@Composable
fun <T> SegmentedSelector(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
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
            Text(
                text = label(option),
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                modifier =
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
                        .padding(4.dp),
            )
        }
    }
}
