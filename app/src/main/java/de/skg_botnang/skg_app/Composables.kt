package de.skg_botnang.skg_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageList(messages: SnapshotStateList<FCMMessage>, debugAction: () -> Unit) {
    val listState = rememberLazyListState()
    val previousFirstMsg: FCMMessage? = remember { messages.getOrNull(0) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row {
            Clock()
            Button(onClick = debugAction) {
                Text("Debug Make Message")
            }
        }
        LazyColumn(
            state=listState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages, key={ it.id }) {
                MessageCard(it)
            }
        }

    }

    if (messages.getOrNull(0) != previousFirstMsg) {
        coroutineScope.launch {
            listState.scrollToItem(index = 0, 0)
        }
    }
}

@Composable
fun Clock() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    LaunchedEffect("Uhr") {
        while(true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }

    Text(
        text = now.format(formatter),
        modifier = Modifier.padding(8.dp),
        color = Color.Magenta
    )
}

@Composable
fun MessageCard(message: FCMMessage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            //.padding(horizontal = 0.dp, vertical = 0.dp
        ,
        shape= RoundedCornerShape(8.dp),
        // Using state in compose:
        // https://developer.android.com/codelabs/jetpack-compose-state?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fcompose%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fjetpack-compose-state
    ) {
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .heightIn(min = 40.dp)
        ) {
            Text("${message.id}: ${message.title}", fontSize=20.sp)
            Text(
                DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm").format(message.time),
                fontSize=12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(message.body)
        }
    }
}