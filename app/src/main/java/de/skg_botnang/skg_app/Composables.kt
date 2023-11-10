package de.skg_botnang.skg_app

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class ScreenShown { NOTIFICATIONS, HOMEPAGE }

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainComposable(viewModel: MessagesViewModel, debugAction: () -> Unit) {
    var currentScreen by remember { mutableStateOf(ScreenShown.NOTIFICATIONS) }

    SKGAppTheme {
        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = { Text("SKG-App") },
                    actions = {
                        IconButton(onClick = { currentScreen = ScreenShown.NOTIFICATIONS }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                       IconButton(onClick = { currentScreen = ScreenShown.HOMEPAGE }) {
                            Icon(Icons.Default.Home, contentDescription = null)
                        }
                    }
                )
            }
        ) { innerPadding ->
            when (currentScreen) {
                ScreenShown.NOTIFICATIONS -> MessageList(innerPadding, viewModel.messages, debugAction = debugAction)
                ScreenShown.HOMEPAGE      -> Homepage(innerPadding)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MessageList(padding: PaddingValues, messages: SnapshotStateList<FCMMessage>, debugAction: () -> Unit) {
    val listState = rememberLazyListState()
    val previousFirstMsg: FCMMessage? = remember { messages.getOrNull(0) }
    val coroutineScope = rememberCoroutineScope()

    Log.d("AAA", "Composing MessageList")
    Column(
        modifier = Modifier
            .padding(padding)
            .padding(8.dp),
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
            listState.animateScrollToItem(index = 0)
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Homepage (padding: PaddingValues) {
    val mUrl = "https://www.skg-botnang.de"
    var backEnabled by remember { mutableStateOf(false) }
    // var webView by remember { mutableStateOf<WebView?>(null) }
    // LocalContext.current is a composable function not allowed inside remember
    val activity = LocalContext.current.let { remember { it as? ComponentActivity } }
    var webView: WebView? = null
    // var webView by remember { mutableStateOf<WebView?>(null) }

    Log.d("AAA", "Composing Homepage, activity=$activity")

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(
        modifier = Modifier.padding(padding),
        factory = {
            Log.d("AAA", "Making WebView")
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        backEnabled = view.canGoBack()
                    }
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                        Log.d("AAA", "OverrideUrlLoading: ${request.url}, ${request.url.host}, $activity")
                        activity ?: return false
                        //val host = Uri.parse(request.url.toString()).host
                        val host = request.url.host
                        if (host != null && host.endsWith("skg-botnang.de", ignoreCase = true)) {
                            // This is your website, so don't override. Let your WebView load
                            // the page.
                            Log.d(TAG, "OverrideUrlLoading: returning false")
                            return false
                        }
                        // Otherwise, the link isn't for a page on your site, so launch another
                        // Activity that handles URLs.
                        Log.d(TAG, "OverrideUrlLoading: starting intent ${request.url}")
                        activity.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                        return true
                    }
                }
                loadUrl(mUrl)
                settings.javaScriptEnabled = true
            }
        },
        update = {
            Log.d("AAA", "AndroidView: update, ${it.url}")
            webView = it
            // it.loadUrl(mUrl)
        }
    )

    BackHandler(enabled = backEnabled) {
        Log.d("AAA", "BackHandler")
        webView?.goBack()
    }
}


/*
@Composable
fun Homepage () {
    val webViewState = rememberWebViewState(url = url)
    WebView(
        // modifier = modifier,
        state = webViewState,
        captureBackPresses = true,
        onCreated = { it : WebView ->
            it.settings.javaScriptEnabled = true
        }
    )
}
*/
