package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme

@Preview
@Composable
fun TestAnima() {
    var offp: Boolean by remember { mutableStateOf(false) }
    val col1 = MaterialTheme.colorScheme.primary
    val col2 = MaterialTheme.colorScheme.secondary
    var col: Color by remember { mutableStateOf(col1) }
//    val color by animateColorAsState(
//        targetValue = if(offp) MaterialTheme.colorScheme.primary
//        else MaterialTheme.colorScheme.secondary
//    )
    Button(
        onClick = {
            offp = !offp
            col = if (offp) col1 else col2
        },
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(col)
    ) {
        Text(
            "Col",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Circle(num: Int, itemSize: Dp) {
    var offp: Boolean by remember { mutableStateOf(false) }
    val offset by animateOffsetAsState(
        if(offp) Offset(0f, -40f) else Offset.Zero,
        tween(500, easing = FastOutSlowInEasing)
        // spring()
    )
    Box(
        modifier = Modifier
            .offset { offset.round() }
            .size(itemSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                offp = !offp
                Log.d("SKG-App", "Num: $num, $offp")
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$num",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun LayoutTestScreen() {
    val numItems = 7
    val itemSize = 70.dp
    val minSpacing = 20.dp
    val coordinates = remember { List(numItems) { mutableStateOf<LayoutCoordinates?>(null) } }
    val highlights = remember { List(numItems) { mutableStateOf(false) } }

    SKGAppTheme(darkTheme = true) {
        RowsWithEqualItems(numItems = numItems, itemSize = itemSize, minSpacing = minSpacing)
    }
}


// Compute the ceiling of the division of two integers
infix fun Int.ceilDiv(b: Int): Int {
    return (this + b - 1) / b
}


@Composable
fun RowsWithEqualItems(numItems: Int, itemSize: Dp, minSpacing: Dp) {
    val sizePlusSpacing = with (LocalDensity.current) {
        (itemSize + minSpacing).roundToPx()
    }
    val itemSizePx = with (LocalDensity.current) { itemSize.roundToPx() }
    val spacingPx = with (LocalDensity.current) { minSpacing.roundToPx() }
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            repeat(numItems) { index -> Circle(index, itemSize) }
        }
    ) { measurables, constraints ->
        val constraints1 = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(constraints1) }
        val width = constraints.maxWidth
        val maxItemsPerRow = width / sizePlusSpacing
        val rows = placeables.size ceilDiv maxItemsPerRow
        val itemsPerRow = placeables.size ceilDiv rows
        var leftMargin =
            (width - itemsPerRow * sizePlusSpacing + spacingPx) / 2
        val height = rows * sizePlusSpacing + 2 * leftMargin - spacingPx

        layout(width, height) {
            var currentY = leftMargin

            placeables.forEachIndexed { index, placeable ->
                // Placeable position in the current row
                val rowPos = index % itemsPerRow

                placeable.placeRelative(
                    x = leftMargin + rowPos * sizePlusSpacing,
                    y = currentY
                )

                // If we are the last item of the row, move to the next row
                if (rowPos == itemsPerRow - 1) {
                    // Move to the next row
                    currentY += sizePlusSpacing

                    // If we are in the last row but one, center the remaining items horizontally
                    val currentRow = index / itemsPerRow + 1 // 1-based
                    if (currentRow == rows - 1) {
                        val itemsInLastRow = placeables.size - (rows - 1) * itemsPerRow
                        leftMargin = (width - itemsInLastRow * sizePlusSpacing + spacingPx) / 2
                    }
                }
            }
        }
    }
}


// Solution of ChatGPT 4

// Hi ChatGPT, let's develop a JetPack Compose function that lays out a number of circles in rows.
// Use the minimum necessary number of rows. Distribute the circles as equally as possible into the
// rows. The circles shall be spaced by a specific distance and they shall be centered in their
// row. The circles are to produced with this composable function: Circle...

// Rewrite CirclesLayout using the Layout composable instead of Column.
@Composable
fun CirclesLayout(totalCircles: Int, itemSize: Dp, spacing: Dp) {
    Layout(
        content = {
            for (i in 0 until totalCircles) {
                Circle(num = i, itemSize = itemSize)
            }
        }
    ) { measurables, constraints ->
        // Calculate the maximum number of circles per row based on the screen width
        val maxCirclesPerRow = ((constraints.maxWidth - spacing.roundToPx()) / (itemSize.toPx() + spacing.toPx())).toInt()

        // Define the size of each item
        val itemConstraints = constraints.copy(minWidth = itemSize.roundToPx(), minHeight = itemSize.roundToPx())

        // Measure each child
        val placeables = measurables.map { measurable ->
            measurable.measure(itemConstraints)
        }

        // Calculate the size of the layout
        val numRows = ceil(totalCircles.toFloat() / maxCirclesPerRow).toInt()
        val layoutWidth = constraints.maxWidth
        val layoutHeight = (itemSize.toPx() * numRows + spacing.toPx() * (numRows - 1)).toInt()

        // Define the layout
        layout(layoutWidth, layoutHeight) {
            var yPosition = 0

            placeables.chunked(maxCirclesPerRow).forEach { row ->
                var xPosition = (layoutWidth - (row.size * itemSize.toPx() + (row.size - 1) * spacing.toPx())) / 2f

                row.forEach { placeable ->
                    placeable.placeRelative(x = xPosition.roundToInt(), y = yPosition)
                    xPosition += itemSize.toPx() + spacing.toPx()
                }

                yPosition += itemSize.roundToPx() + spacing.roundToPx()
            }
        }
    }
}
