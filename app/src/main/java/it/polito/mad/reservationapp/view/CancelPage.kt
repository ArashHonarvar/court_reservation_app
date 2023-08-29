package it.polito.mad.reservationapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DateDisplayer
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.SportTimeslotDisplayer
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun CancelPage(reservationID: String, navController: NavController, mainViewModel: AppViewModel = viewModel(), firebaseViewModel: FirebaseViewModel = viewModel()) {
    //val reservation = mainViewModel.repo.getReservationById(reservationID).observeAsState()
    //val court = mainViewModel.repo.getCourtByReservationID(reservationID).observeAsState()

    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    val reservation by remember { firebaseViewModel.getReservationsById(reservationID.toString()) }.observeAsState()
    val court by remember { firebaseViewModel.getCourtByReservationId(reservationID.toString()) }.observeAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {
        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "Cancel Reservation", Modifier.align(Alignment.CenterHorizontally))
            Row() {
                Column(
                    modifier = Modifier.weight(0.3F)
                ) {
                    val df_in = SimpleDateFormat("dd/MM/yyyy")
                    val df_out = SimpleDateFormat("dd MMM")
                    if(reservation !== null)
                        DateDisplayer(text = df_out.format(inputDateFormat.parse(reservation!!.reserved_date.toString())))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(0.7F)
                ) {
                    SportTimeslotDisplayer(
                        sport = court?.getOrNull()?.sport_name?:"",
                        timeslots = reservation?.timeslots ?: emptyList()
                    )
                }
            }
            InfoText(text = "Do you confirm the cancellation of this reservation?", Modifier.align(Alignment.CenterHorizontally))
            CustomButton(
                text = "Delete",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if(reservation!=null && court?.getOrNull()!=null){
                        //mainViewModel.deleteReservation(reservationID,reservation!!.getOrNull()?.reserved_date.toString(), court!!.getOrNull()?.id!!.toInt())
                        firebaseViewModel.deleteReservation(reservation!!.id)
                        navController.navigate("MyReservations")
                    }
                }
            )
        }
    }
}