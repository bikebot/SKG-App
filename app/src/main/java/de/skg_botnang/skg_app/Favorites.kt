package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun FavoritesScreen() {
    val sizes = listOf(true, false, true, true, true, false, true, true, false)
    val coordinates = remember { sizes.map { mutableStateOf<LayoutCoordinates?>(null) } }
    SKGAppTheme(darkTheme = true) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
/*
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    Log.d("SKG-App", "Drag: $dragAmount")
                }
            }
*/
        ) {
            sizes.forEachIndexed { index, small ->
                Item(index, small, coordinates)
            }
            // Add a card with a line of text for each item in the list
/*
            Card {
                coordinates.forEachIndexed { index, coords ->
                    Text(text = "$index: ${coords?.positionInParent()}, ${coords?.size}")
                }
            }
*/
        }
    }
}

@Composable
fun Item(index: Int, small: Boolean = false, coordinates: List<MutableState<LayoutCoordinates?>>) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }
    Card(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(if (small) 0.5f else 1f)
            .padding(end = 10.dp)
            .offset { offset }
            .onGloballyPositioned { coords ->
                if(index > 0) {
                    Log.d("SKG-App", "Previous: ${coordinates[index - 1].value?.positionInParent()}")
                }
                coordinates[index].value = coords
                // Log.d("SKG-App", "$index: Coordinates: ${coordinates[index]?.positionInParent()}, ${coordinates[index]?.size}")
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount.round()
                    Log.d("SKG-App", "Offset: $offset")
                }
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        // Add a button and a text to the card
        Button(onClick = { /*TODO*/ }) {
            // Create a text containing the index of the Item
            val coords = coordinates[index].value
            val pos = coords?.positionInParent()?.let { "${it.x}, ${it.y}" }
            Text(text = "$index: $pos, ${coords?.size}")
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
//@Preview
@Composable
fun Favorites() {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Compute the screen size
/*
        listOf(true, false, true, true, true, false, true, true, false).forEachIndexed { index, small ->
            Item(index, small, coordinates
        }
*/
    }
}
