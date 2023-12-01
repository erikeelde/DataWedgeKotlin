package com.example.datawedgerepository

import android.os.Bundle
import androidx.core.os.bundleOf

data class KeyboardOutputConfiguration(
    val enabled: Boolean,
) {
    fun asBundle(): Bundle = bundleOf(
        "PLUGIN_NAME" to "KEYSTROKE",
        "PARAM_LIST" to bundleOf(
            "keystroke_output_enabled" to enabled.toString(),
        )
    )
}
