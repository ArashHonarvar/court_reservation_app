package it.polito.mad.reservationapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.CustomCalendar
import it.polito.mad.reservationapp.view.components.DateDisplayer
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.SportTimeslotDisplayer
import it.polito.mad.reservationapp.view.components.Toggle
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MyReservationsPage(navController: NavController, userViewModel: UserViewModel) {
//    val reservationsByUser by remember { firebaseViewModel.getReservationsByUserId("user1") }.observeAsState()

    val reservationsByUser by remember { userViewModel.userReservations }.observeAsState()

    val (isCompact, setIsCompact) = remember {
        mutableStateOf(false)
    }
    val (selectedDate, setSelectedDate) = remember {
        mutableStateOf("")
    }

    val setSingleDateView: (date: String)->Unit = {
        setSelectedDate(it)
        setIsCompact(true)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {

        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "My Reservations", Modifier.align(Alignment.CenterHorizontally))

            if(isCompact){
                CompactMyReservations(reservationsByUser?: emptyList(), selectedDate, isCompact, setIsCompact, navController)
            }
            else if(selectedDate!=""){
                CompactMyReservations(reservationsByUser?: emptyList(), selectedDate, true, setIsCompact, navController)
            }
            else{
                InfoText(text = "Select a day to show your active reservations", Modifier.align(Alignment.CenterHorizontally))
                Toggle(text = "Compact view", modifier = Modifier.align(Alignment.End) ,isCompact, setIsCompact)
                CustomCalendar(reservationsByUser?: emptyList(), setSelectedDate, emptyList())
            }
        }
    }
}

@Composable
fun CompactMyReservations(reservations: List<Reservation>, selectedDate: String, isCompact: Boolean, setIsCompact: (checked:Boolean) -> Unit, navController: NavController, mainViewModel: AppViewModel = viewModel()){
    val dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val (isShowHistory, setIsShowHistory) = remember {
        mutableStateOf(false)
    }

    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    val (reservationList, setReservationList) = remember {
        mutableStateOf(reservations.filter { LocalDate.parse(df.format(inputDateFormat.parse(it.reserved_date.toString())),dbFormatter) >= LocalDate.now() })
    }

    if(selectedDate==""){
        Column() {
            if (isShowHistory) {
                setReservationList(reservations.filter { LocalDate.parse(df.format(inputDateFormat.parse(it.reserved_date.toString())),dbFormatter) < LocalDate.now() })
                CustomButton(text = "Show Active", onClick = { setIsShowHistory(false) })
            } else {
                setReservationList(reservations.filter { LocalDate.parse(df.format(inputDateFormat.parse(it.reserved_date.toString())),dbFormatter) >= LocalDate.now() })
                CustomButton(text = "Show History", onClick = { setIsShowHistory(true) })
            }
            Toggle(
                text = "Compact view",
                modifier = Modifier.align(Alignment.End),
                isCompact,
                setIsCompact
            )
            ReservationDisplayer(reservations = reservationList, navController = navController)
        }
    }
    else{
        ReservationDisplayer(reservations = reservations.filter{df.format(inputDateFormat.parse(it.reserved_date.toString()))==selectedDate}, navController = navController)
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ReservationDisplayer(reservations: List<Reservation>, navController: NavController){
    val groupedReservations = reservations.groupBy { it.reserved_date }

    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val df = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedReservations.forEach { (date, reservations) ->
                item {
                    Row(

                    ) {
                        Column(
                            modifier = Modifier.weight(0.4F)
                        ) {
                            val df_in = SimpleDateFormat("dd/MM/yyyy")
                            val df_out = SimpleDateFormat("dd MMM")
                            var displayedDate: String = "NoDate"
                            displayedDate = df.format(inputDateFormat.parse(date.toString()))
                            DateDisplayer(text = displayedDate)
                        }
                        Column(
                            modifier = Modifier.weight(0.6F),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            reservations.forEach { r->
                                val bundle = bundleOf()
                                bundle.putString("reservationID", r.id)
                                SportTimeslotDisplayer(
                                    sport = r.court?.sport_name?:"",
                                    timeslots = r.timeslots,
                                    onClick = { navController.navigate("DetailsPage/${r.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}