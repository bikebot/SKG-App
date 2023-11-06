package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessagesViewModel(): ViewModel() {
    val messages = mutableStateListOf<FCMMessage>()

    init {
        Log.d(TAG, "MessagesViewModel: init")
    }

    private var dbRead = false

    fun readFromDb(dao: FCMMessageDao) {
        if (!dbRead) {
            CoroutineScope(Dispatchers.IO).launch {
                // viewModelScope.launch(Dispatchers.IO) {
                messages.addAll(dao.getSome(12))
            }
            dbRead = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "MessagesViewModel: onCleared")
    }
}