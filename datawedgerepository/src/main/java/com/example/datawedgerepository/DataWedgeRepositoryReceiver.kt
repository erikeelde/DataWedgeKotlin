package com.example.datawedgerepository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.shareIn

private const val RECEIVED_INTENTS_BUFFER = 5

internal class DataWedgeRepositoryReceiver(scope: CoroutineScope) : BroadcastReceiver() {

    private val receivedIntentsChannel: Channel<Intent> = Channel(RECEIVED_INTENTS_BUFFER)
    val intents = receivedIntentsChannel.consumeAsFlow()
        .shareIn(scope, SharingStarted.Eagerly, RECEIVED_INTENTS_BUFFER)

    override fun onReceive(context: Context, p1: Intent) {
        receivedIntentsChannel.trySend(p1)
    }
}
