package com.timeofmylife.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.timeofmylife.ui.theme.ScreenHorizontalPadding

@Composable
fun BoxScope.AddFab(
    onClick: () -> Unit,
    contentDescription: String,
    bottomPadding: Dp,
) {
    SmallFloatingActionButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = ScreenHorizontalPadding,
                    bottom = bottomPadding + ScreenHorizontalPadding,
                ),
    ) {
        Icon(Icons.Default.Add, contentDescription = contentDescription)
    }
}
