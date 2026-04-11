package com.timeofmylife.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
    label: String,
    color: Color,
) {
    Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}
