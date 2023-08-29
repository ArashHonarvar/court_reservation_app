package it.polito.mad.reservationapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.view.components.CustomCalendar
import it.polito.mad.reservationapp.view.components.DropdownMenuDemo
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import java.text.SimpleDateFormat

@Composable
fun BookPage(navController: NavController, firebaseViewModel: FirebaseViewModel = viewModel()) {

    val sports by remember { firebaseViewModel.getSports() }.observeAsState()

    val (selectedSport, setSelectedSport) = remember {
        mutableStateOf(Pair<String,String>("0",""))
    }

    val allReservations by remember { firebaseViewModel.getAllReservations() }.observeAsState()

    val (notAvailableDays, setNotAvailableDays) = remember {
        mutableStateOf(emptyList<String>())
    }

    val (sportMap, setSportMap) = remember {
        mutableStateOf(emptyMap<String,String>())
    }

    LaunchedEffect(sports) {
        if (sports != null && sports != {}) {
            val newPairs: MutableList<Pair<String, String>> = sports!!.getOrDefault(emptyList())
                ?.mapIndexed { index, element ->
                    (index + 1).toString() to element
                }?.toMutableList() ?: mutableListOf()

            setSportMap(newPairs.toMap())
        }
    }

    LaunchedEffect(allReservations) {
        val bookedCourtsBySport = allReservations?.filter { it.court?.sport_name == selectedSport.second }.orEmpty()

        println("1 $bookedCourtsBySport")
        println("1 ${bookedCourtsBySport.groupBy { it.reserved_date }}")

        bookedCourtsBySport.groupBy { it.reserved_date }.forEach { bk ->
            if(bk.value.groupBy { it.court?.id }.all {
                    println("3 $it")
                    println("4 ${it.value.flatMap { l -> l.timeslots }}")
                    if(it.value.flatMap { l -> l.timeslots }.size== it.value[0].court?.timeslots?.size){
                        println(it)
                        true
                    }
                    else{println(it)
                        false}
                }){
                setNotAvailableDays(notAvailableDays.plus(bk.key.toString()))
            }
        }
    }

    LaunchedEffect(selectedSport){
        val bookedCourtsBySport = allReservations?.filter { it.court?.sport_name == selectedSport.second }.orEmpty()

        println("1 $bookedCourtsBySport")
        println("1 ${bookedCourtsBySport.groupBy { it.reserved_date }}")

        bookedCourtsBySport.groupBy { it.reserved_date }.forEach { bk ->
            if(bk.value.groupBy { it.court?.id }.all {
                    println("3 $it")
                    println("4 ${it.value.flatMap { l -> l.timeslots }}")
                    if(it.value.flatMap { l -> l.timeslots }.size== it.value[0].court?.timeslots?.size){
                        println(it)
                        true
                    }
                    else{println(it)
                        false}
                }){
                setNotAvailableDays(notAvailableDays.plus(bk.key.toString()))
            }
        }
    }

    LaunchedEffect(sportMap) {
        if (sportMap["1"] != null) {
            setSelectedSport(Pair("1", sportMap["1"].toString()))
        }
    }


    val (selectedDate, setSelectedDate) = remember{
        mutableStateOf("")
    }

    if(selectedDate!=""){
        val d_temp = selectedDate
        setSelectedDate("")
        val df_in = SimpleDateFormat("dd/MM/yyyy")
        val df_out = SimpleDateFormat("yyyyMMdd")
        val d = df_out.format(df_in.parse(d_temp))

        println("PRINT IN THE SELECTED DATE: ${selectedSport.second}, $d")

        navController.navigate("CourtAvailabilityPage/${selectedSport.second}/${d}")
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {
        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "Book a court", Modifier.align(Alignment.CenterHorizontally))
            InfoText(text = "Select a day to show the available courts", Modifier.align(Alignment.CenterHorizontally))
            DropdownMenuDemo(items = sportMap.ifEmpty { emptyMap<String,String>() }, selectedSport, setSelectedSport)
            Spacer(modifier = Modifier.height(16.dp))
            println("NOT AVAILABLE $notAvailableDays")
            CustomCalendar(listOf(), setSelectedDate, notAvailableDays, true)
        }
    }
}