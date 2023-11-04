package de.skg_botnang.skg_app

// https://firebase.blog/posts/2023/08/adding-fcm-to-jetpack-compose-app/
// https://github.com/firebase/quickstart-android/tree/master/messaging/app
// https://github.com/FirebaseExtended/make-it-so-android/tree/main/start/app/src/main/java/com/example/makeitso

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val messages = mutableStateListOf(
        FCMMessage(
        0,
        "Wasserschaden",
        "Die Fußballumkleide steht unter Wasser"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SKGAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageList(messages)
                }
            }
        }
        askNotificationPermission()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "broadcastReceiver: action = ${intent?.action}")
            if ("de.skg_botnang.skg_app.NOTIFY_FCM" == intent?.action) {
                val id = intent.getLongExtra("rowID", -1)
                Log.d(TAG, "broadcastReceiver: id = $id")
                val database = FCMDatabase.getDatabase(applicationContext)
                val messageDao = database.messageDao()
                // Handle the received data
                CoroutineScope(Dispatchers.IO).launch {
                    val msg = messageDao.get(id)
                    messages.add(0, msg)
                    Log.d(TAG, "Message found: $msg")
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            msg.title + "\n" + msg.body,
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, IntentFilter("de.skg_botnang.skg_app.NOTIFY_FCM"), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, IntentFilter("de.skg_botnang.skg_app.NOTIFY_FCM"))
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }
}

/*
@Preview(showBackground = true)
@Composable
fun MessagePreview() {
    SKGAppTheme {
        MessageList()
    }
}
*/

val TAG = "SKG-App"