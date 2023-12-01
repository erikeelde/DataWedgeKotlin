package com.example.datawedgerepository

import android.os.Bundle
import androidx.core.os.bundleOf

enum class IntentOutputIntentDelivery(val datawedgeConstant: Int) {
    Activity(0), Service(1), Broadcast(2)
}

// https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/#scannerinputparameters
data class IntentOutputConfiguration(
    val enabled: Boolean,
    val intentAction: String,
    val intentDelivery: IntentOutputIntentDelivery
) {
    fun asBundle(): Bundle = bundleOf(
        "PLUGIN_NAME" to "INTENT",
        "PARAM_LIST" to bundleOf(
            "intent_output_enabled" to enabled.toString(),
            "intent_action" to intentAction,
            "intent_delivery" to intentDelivery.datawedgeConstant.toString()
        )
    )
}
