package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers


class MessagesViewModel(dao: FCMMessageDao): ViewModel() {
    val messages = mutableStateListOf<FCMMessage>()

    init {
        Log.d(TAG, "MessagesViewModel: init")
        CoroutineScope(Dispatchers.IO).launch {
            messages.addAll(dao.getSome(12))
        }
    }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "MessagesViewMOdel: onCleared")
    }
}