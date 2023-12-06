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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val viewModel: MessagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity: onCreate")
        super.onCreate(savedInstanceState)

        // applicationContext is only available in onCreate
        viewModel.readFromDb(FCMDatabase.getDatabase(applicationContext).messageDao())

        setContent {
            // MainComposable(viewModel, { debugMakeMesssage() })
            FavoritesScreen()
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
                    viewModel.messages.add(0, msg)
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

    fun debugMakeMesssage() {
        val database = FCMDatabase.getDatabase(applicationContext)
        val messageDao = database.messageDao()
        // Handle the received data
        CoroutineScope(Dispatchers.IO).launch {
            // We need to update the object after inserting it to obtain db auto-generated
            // values, e.g., the id
            val msg = messageDao.get(messageDao.insert(FCMMessage(
                "Debug ${debugMsgNum++}",
                "This message was created by a button click."
            )))
            viewModel.messages.add(0, msg)
            Log.d(TAG, "Debug message made: $msg")
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    msg.title + "\n" + msg.body,
                    Toast.LENGTH_LONG,
                ).show()
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity: onDestroy")
    }

    companion object {
        private var debugMsgNum = 0
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

const val TAG = "SKG-App"