package it.polito.mad.reservationapp.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun FeedbackAlert(setOpenDialog: (Boolean)-> Unit, type: String, navController: NavController) {
    AlertDialog(
    onDismissRequest = {
        setOpenDialog(false)
    },
    title = {
        Text(text = when(type) {
            "successNew" -> "Congratulations!"
            "successEdit" -> "Congratulations!"
            "errorNoTimeslots" -> "Attention!"
            "errorWrongTimeslots" -> "Attention!"
            else -> {""}
        })
    },
    text = {
        Text(text = when(type) {
            "successNew" -> "You successfully booked a court. "
            "successEdit" -> "Your changes have been correctly applied. "
            "errorNoTimeslots" -> "Please select at least a timeslot. "
            "errorWrongTimeslots" -> "Please only select adjacent timeslots. "
            else -> {""}
        })
    },
    confirmButton = {
        Button(
            onClick = {
                setOpenDialog(false)
                if(type.contains("success")) navController.navigate("MyReservations")
            }) {
            Text("Ok")
        }
    },
    dismissButton = {
        Button(
            onClick = {
                setOpenDialog(false)
                if(type.contains("success")) navController.navigate("MyReservations")
            }) {
            Text("Close")
        }
    },
    titleContentColor = when(type) {
            "successNew" -> Color.Green
            "successEdit" -> Color.Green
            "errorNoTimeslots" -> Color.Red
            "errorWrongTimeslots" -> Color.Red
            else -> {Color.Gray}
        }
    )
}