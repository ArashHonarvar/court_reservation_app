package it.polito.mad.reservationapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DateDisplayer
import it.polito.mad.reservationapp.view.components.DisabledButton
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.TimeslotDisplayer
import it.polito.mad.reservationapp.view.components.buildTimeslotVisualization
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun ReservationDetailsPage(reservationID: String, navController: NavController, mainViewModel: AppViewModel = viewModel(), firebaseViewModel: FirebaseViewModel = viewModel()) {
    // Query the model to give the correct reservation by id
    //val reservation = mainViewModel.repo.getReservationById(reservationID).observeAsState()
    //val court = mainViewModel.repo.getCourtByReservationID(reservationID).observeAsState()

    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    val dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val reservation by remember { firebaseViewModel.getReservationsById(reservationID)}.observeAsState()

    val (court, setCourt) = remember { mutableStateOf(reservation?.court) }

    LaunchedEffect(reservation) {
        if (reservation != null) {
            setCourt(reservation!!.court)
        }
    }

    val (equipmentText, setEquipmentText) = remember{
        mutableStateOf("")
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {

        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "Reservation Details", Modifier.align(Alignment.CenterHorizontally))
            // First row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                val df_out = SimpleDateFormat("dd MMM")
                var displayedDate: String = "NoDate"
                if(reservation?.reserved_date!=null)
                    displayedDate = df_out.format(inputDateFormat.parse(reservation?.reserved_date.toString()))
                DateDisplayer(text = displayedDate)
                Spacer(modifier = Modifier.width(8.dp))
                DateDisplayer(text = buildTimeslotVisualization(reservation?.timeslots?: emptyList()).replace("\n", "-") ?: "NoTimeslot")
            }

            // Second row
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    //.weight(1f)
                    .height(300.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = court?.sport_name?: "NoSportName",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = court?.name?: "NoCurtName",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.weight(5f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        if (reservation?.review != null) {
                            DisabledButton(text = "Rated")
                        } else if (reservation?.reserved_date != null) {
                            if (LocalDate.parse(df.format(inputDateFormat.parse(reservation!!.reserved_date.toString())!!),dbFormatter) >= LocalDate.now()) {
                                DisabledButton(text = "Future")
                            }else{
                                CustomButton(text = "Rate", onClick = {navController.navigate("RatingPage/${court?.id?:"NoCourt"}/${reservationID}")},modifier = Modifier.weight(5f))
                            }
                        } else {
                            CustomButton(text = "Rate", onClick = {navController.navigate("RatingPage/${court?.id?:"NoCourt"}/${reservationID}")},modifier = Modifier.weight(5f))
                        }
                    }
                    if(reservation?.equipment_requested == true)
                        setEquipmentText("You requested the equipment")
                    else
                        setEquipmentText("No equipment requested")
                    Text(
                        text = equipmentText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if(Date().compareTo(reservation?.reserved_date?:Date())<=0){
                // Third row
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomButton(text = "Edit", modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate("EditReservation/${reservationID}/${court?.id?: "NoCourt"}/${court?.sport_name?: "NoSport"}")
                        }
                    )
                    CustomButton(text = "Delete", modifier = Modifier.weight(1f), onClick = {navController.navigate("CancelPage/${reservationID}")})
                }
            }
        }
    }
}