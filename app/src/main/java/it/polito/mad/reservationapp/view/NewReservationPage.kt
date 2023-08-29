package it.polito.mad.reservationapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import it.polito.mad.reservationapp.view.components.CourtWithRating
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DateDisplayer
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.SportDisplayer
import it.polito.mad.reservationapp.view.components.TimeslotDisplayer
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.view.components.FeedbackAlert
import it.polito.mad.reservationapp.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun NewReservationPage(courtID: String, date: String, navController: NavController, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel = viewModel()){
    val user by remember { userViewModel.user }.observeAsState()
    val court by remember { firebaseViewModel.getCourtById(courtID) }.observeAsState()
    val rating by remember { firebaseViewModel.getReviewByCourtId(courtID) }.observeAsState()
    val availableTimeslots by remember { firebaseViewModel.getAvailableTimeslotByCourtIDAndDate(courtID ?: "", date) }.observeAsState()

    val (equipment, setEquipment) = remember { mutableStateOf(false) }
    val (sport, setSport) = remember { mutableStateOf(court?.getOrNull()?.sport_name) }
    val (openErrorNoTimeslotsDialog, setOpenErrorNoTimeslotsDialog) = remember { mutableStateOf(false) }
    val (openErrorWrongTimeslotsDialog, setOpenErrorWrongTimeslotsDialog) = remember { mutableStateOf(false) }
    val (openSuccessDialog, setOpenSuccessDialog) = remember { mutableStateOf(false) }
    val (selectedTimeslots, setSelectedTimeslots) = remember { mutableStateOf(emptyMap<Int,String>()) }

    LaunchedEffect(court) {
        if (court?.getOrNull() != null)
            setSport(court!!.getOrNull()!!.sport_name)
    }

    val df_in = SimpleDateFormat("yyyyMMdd")
    val df_out = SimpleDateFormat("dd/MM/yyyy")

    val handleSaveReservation: ()->Unit = {
        firebaseViewModel.newReservation(
            Reservation(
                "",
                Timestamp.now(),
                createTimestampFromString(date),
                equipment, court?.getOrNull(),
                selectedTimeslots.values.toList().sorted(),
                user,
                null
            )
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {
        Column(Modifier.padding(16.dp)) {
            if (openErrorNoTimeslotsDialog) {
                FeedbackAlert(setOpenDialog = setOpenErrorNoTimeslotsDialog, type = "errorNoTimeslots", navController = navController)
            }
            if (openSuccessDialog) {
                FeedbackAlert(setOpenDialog = setOpenSuccessDialog, type = "successNew", navController = navController)
            }
            if (openErrorWrongTimeslotsDialog) {
                FeedbackAlert(setOpenDialog = setOpenErrorWrongTimeslotsDialog, type = "errorWrongTimeslots", navController = navController)
            }
            PageTitle(title = "New Reservation", Modifier.align(Alignment.CenterHorizontally))
            // First row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                val df_out_text = SimpleDateFormat("dd MMM")
                val displayedDate: String = df_out_text.format(df_in.parse(date?:"")!!)
                DateDisplayer(displayedDate)
                Spacer(modifier = Modifier.width(8.dp))
                SportDisplayer(sport ?:"Sport")
            }

            // Second row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                CourtWithRating(court = court?.getOrNull()?.name ?: "", rating = rating?.getOrNull() ?: 0f)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // specify the number of columns
                modifier = Modifier.weight(1F),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(
                    (availableTimeslots?.getOrNull() ?: emptyList())
                        .sorted()
                )
                { index, it ->
                    Box(
                        Modifier
                            .padding(8.dp)
                            .clickable {
                                if (selectedTimeslots.values.contains(it))
                                    setSelectedTimeslots(selectedTimeslots.filter { t -> t.value != it })
                                else
                                    setSelectedTimeslots(
                                        selectedTimeslots.plus(
                                            Pair<Int, String>(
                                                index + 1,
                                                it
                                            )
                                        )
                                    )
                            }
                    ) {

                        TimeslotDisplayer(text = it.replace("-", "\n"), selectedTimeslots.values.contains(it))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                InfoText(
                    text = "Do you need the equipment?",
                    Modifier.align(Alignment.CenterVertically)
                )
                Checkbox(
                    checked = equipment,
                    onCheckedChange = { setEquipment(!equipment) },
                    Modifier.align(Alignment.CenterVertically)
                )
            }

            // Third row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(text = "Book", modifier = Modifier.weight(1f), onClick = {
                    if(selectedTimeslots.isEmpty()){
                        setOpenErrorNoTimeslotsDialog(true)
                    }else if(!areTimeslotsCoherent(selectedTimeslots.values.toList().sorted())){
                        setOpenErrorWrongTimeslotsDialog(true)
                    }
                    else {
                        handleSaveReservation()
//                        firebaseViewModel.newReservation(
//                            Reservation(
//                                "",
//                                Timestamp.now(),
//                                createTimestampFromString(date),
//                                equipment,
//                                court?.getOrNull(),
//                                selectedTimeslots.values.toList().sorted(),
//                                user,
//                                review = null
//                            )
//                        )

                        setOpenSuccessDialog(true)
                    }
                })
                CustomButton(text = "Cancel", modifier = Modifier.weight(1f)) {
                    navController.navigateUp()
                }
            }
        }
    }
}

fun createTimestampFromString(dateString: String): Date {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
    val date: Date = dateFormat.parse(dateString)

    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}

fun areTimeslotsCoherent(selectedTimeslots: List<String>): Boolean {
    for (i in 0 until selectedTimeslots.size - 1) {
        val currentTimeSlot = selectedTimeslots[i]
        val nextTimeSlot = selectedTimeslots[i + 1]

        val currentTimeSlotEnd = currentTimeSlot.substringAfter("-").trim()
        val nextTimeSlotStart = nextTimeSlot.substringBefore("-").trim()

        if (currentTimeSlotEnd != nextTimeSlotStart) {
            return false
        }
    }
    return true
}