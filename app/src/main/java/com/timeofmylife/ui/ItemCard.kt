package com.timeofmylife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.CardBorderWidth

@Composable
fun ItemCard(
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRect(
                            color = borderColor,
                            size = androidx.compose.ui.geometry.Size(CardBorderWidth.toPx(), size.height),
                        )
                    }
                    .padding(start = 16.dp, end = 12.dp, top = 7.dp, bottom = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
