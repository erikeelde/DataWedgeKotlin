package com.darryncampbell.datawedgekotlin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darryncampbell.datawedgekotlin.ui.theme.MyApplicationTheme

@Preview
@Composable
fun PreviewSwitchWithLabel() {
    MyApplicationTheme {
        Surface {
            SwitchWithLabel(
                checked = true,
                onCheckedChange = {},
                labelContent = { Text("Label") }
            )
        }
    }
}

@Composable
fun SwitchWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    labelContent: (@Composable () -> Unit) = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                // This is for removing ripple when Row is clicked
                indication = null,
                role = Role.Switch,
                onClick = {
                    onCheckedChange(!checked)
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {

        labelContent()
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
            },

        )
    }
}