package it.polito.mad.reservationapp.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun DeleteConfirmationAlert(setOpenDialog: (Boolean)-> Unit, onConfirm: ()->Unit) {
    AlertDialog(
        onDismissRequest = {
            setOpenDialog(false)
        },
        title = {
            Text(text = "Confirm deletion")
        },
        text = {
            Text(text = "Are you sure you want to delete this item?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    setOpenDialog(false)
                }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    setOpenDialog(false)
                }) {
                Text("Cancel")
            }
        },
        titleContentColor = Color.Red
    )
}