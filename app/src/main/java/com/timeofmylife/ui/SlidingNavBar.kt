package com.timeofmylife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.timeofmylife.ui.theme.Accent
import com.timeofmylife.ui.theme.AccentContainer
import com.timeofmylife.ui.theme.DialogCornerRadius
import com.timeofmylife.ui.theme.NavBarHeight

@Composable
fun SlidingNavBar(
    screens: List<Screen>,
    pagerState: PagerState,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { DialogCornerRadius.toPx() }
    val horizontalInsetPx = with(density) { 8.dp.toPx() }
    val verticalInsetPx = with(density) { 6.dp.toPx() }
    var totalWidthPx by remember { mutableIntStateOf(0) }

    val currentPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(NavBarHeight)
                    .onGloballyPositioned { totalWidthPx = it.size.width }
                    .drawBehind {
                        if (totalWidthPx > 0) {
                            val itemWidth = size.width / screens.size
                            val indicatorLeft = currentPosition * itemWidth + horizontalInsetPx
                            val indicatorWidth = itemWidth - 2 * horizontalInsetPx
                            val indicatorHeight = size.height - 2 * verticalInsetPx
                            drawRoundRect(
                                color = AccentContainer,
                                topLeft = Offset(indicatorLeft, verticalInsetPx),
                                size = Size(indicatorWidth, indicatorHeight),
                                cornerRadius = CornerRadius(cornerRadiusPx),
                            )
                        }
                    },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            screens.forEachIndexed { index, screen ->
                val isSelected = pagerState.currentPage == index
                val itemColor = if (isSelected) Accent else unselectedColor
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable { onNavigate(index) }
                            .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        screen.icon,
                        contentDescription = screen.label,
                        tint = itemColor,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = itemColor,
                    )
                }
            }
        }
    }
}
