package it.polito.mad.reservationapp.view

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
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DateDisplayer
import it.polito.mad.reservationapp.view.components.DropdownMenuDemo
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.SportDisplayer
import it.polito.mad.reservationapp.view.components.TimeslotDisplayer
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.view.components.FeedbackAlert
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditReservationPage(reservationID: String, courtID: String, sportName: String, navController: NavController, mainViewModel: AppViewModel = viewModel(), firebaseViewModel: FirebaseViewModel = viewModel()){

    val reservation by remember { firebaseViewModel.getReservationsById(reservationID)}.observeAsState()

    /*val (sportName, setSportName) = remember {
        mutableStateOf("")
    }*/

    val (date, setDate) = remember {
        mutableStateOf("")
    }
    val (equipment, setEquipment) = remember {
        mutableStateOf(false)
    }
    val (selectedTimeslots, setSelectedTimeslots) = remember {
        mutableStateOf(emptyMap<Int,String>())
    }

    val (openErrorNoTimeslotsDialog, setOpenErrorNoTimeslotsDialog) = remember {
        mutableStateOf(false)
    }

    val (openErrorWrongTimeslotsDialog, setOpenErrorWrongTimeslotsDialog) = remember {
        mutableStateOf(false)
    }

    val (openSuccessDialog, setOpenSuccessDialog) = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(reservation) {
        if (reservation != null) {
            setEquipment(reservation!!.equipment_requested)
            setDate(reservation!!.reserved_date.toString())
            //setSportName(reservation!!.getOrNull()!!.court?.sport_name ?: "")

            val newPairs: MutableList<Pair<Int, String>> = reservation!!.timeslots
                .mapIndexed { index, element ->
                    (index + 1) to element
                }.toMutableList() ?: mutableListOf()

            setSelectedTimeslots(newPairs.toMap())
        }
    }

    val courts by remember { firebaseViewModel.getCourtsBySportName(sportName) }.observeAsState()

    val (selectedCourt, setSelectedCourt) = remember {
        mutableStateOf(Pair("", ""))
    }

    val (courtMap, setCourtMap) = remember {
        mutableStateOf(emptyMap<String,String>())
    }

    //val bookedTimeslots = mainViewModel.repo.getBookedTimeslotByCourtIDAndDate(courtID.toInt(), date).observeAsState()

    val availableTimeslots by remember(selectedCourt, date) {
        firebaseViewModel.getAvailableTimeslotByCourtIDAndDate(
            selectedCourt.first.ifEmpty { courtID },
            date
        )
    }.observeAsState()

    LaunchedEffect(selectedCourt) {
        if (selectedCourt.first.isNotEmpty()) {
            firebaseViewModel.getAvailableTimeslotByCourtIDAndDate(selectedCourt.first, date)
        }
    }

    LaunchedEffect(courts) {
        if(courts?.getOrNull() != null) {
            val newPairs: MutableList<Pair<String, String>> = courts!!.getOrDefault(emptyList())
                ?.map { element ->
                    element.id to element.name
                }?.toMutableList() ?: mutableListOf()

            setCourtMap(newPairs.toMap())

            if (courts?.getOrNull()?.any { it.id == courtID } == true) {
                setSelectedCourt(Pair(courtID, newPairs.toMap()[courtID] ?: ""))
            }
        }
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
                FeedbackAlert(setOpenDialog = setOpenSuccessDialog, type = "successEdit", navController = navController)
            }
            if (openErrorWrongTimeslotsDialog) {
                FeedbackAlert(setOpenDialog = setOpenErrorWrongTimeslotsDialog, type = "errorWrongTimeslots", navController = navController)
            }
            PageTitle(title = "Edit Reservation", Modifier.align(Alignment.CenterHorizontally))
            // First row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                val df_in = SimpleDateFormat("dd/MM/yyyy")
                val df_out = SimpleDateFormat("dd MMM")
                var displayedDate: String = "NoDate"

                val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                if(reservation?.reserved_date!=null)
                    displayedDate = df_out.format(inputDateFormat.parse(reservation!!.reserved_date.toString()))
                DateDisplayer(displayedDate)
                Spacer(modifier = Modifier.width(8.dp))
                SportDisplayer(sportName)
            }

            // Second row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {

                println(courtMap)

                DropdownMenuDemo(
                    items = courtMap.ifEmpty { emptyMap<String,String>() },
                    selectedString = Pair(selectedCourt.first ?: "", selectedCourt.second ?: ""),
                    setSelectedString = setSelectedCourt
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // specify the number of columns
                modifier = Modifier.weight(1F),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(
                    if (selectedCourt.first == reservation?.court?.id) {
                        (availableTimeslots?.getOrNull() ?: emptyList())
                            .plus(reservation?.timeslots ?: emptyList()).distinct().sorted()
                    } else {
                        (availableTimeslots?.getOrNull() ?: emptyList()).distinct().sorted()
                    }
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

                        TimeslotDisplayer(text = it.replace("-","\n"), selectedTimeslots.values.contains(it))
                    }
                }
            }

            Row(
                modifier = Modifier
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
                CustomButton(text = "Save", modifier = Modifier.weight(1f), onClick = {
                    if(selectedTimeslots.isEmpty()){
                        setOpenErrorNoTimeslotsDialog(true)
                    }else if(!areTimeslotsCoherent(selectedTimeslots.values.toList().sorted())){
                        setOpenErrorWrongTimeslotsDialog(true)
                    }
                    else {
                        firebaseViewModel.editReservation(
                            Reservation(
                                reservationID,
                                Timestamp.now(),
                                reservation?.reserved_date!!,
                                equipment,
                                Court(
                                    selectedCourt.first,
                                    selectedCourt.second,
                                    "",
                                    emptyList(),
                                    ""
                                ),
                                selectedTimeslots.values.toList().sorted(),
                                reservation?.user!!,
                                null
                            )
                        )
                        setOpenSuccessDialog(true)
                    }
                })
                CustomButton(text = "Delete", modifier = Modifier.weight(1f)) {
                    navController.navigate("CancelPage/${reservationID}")
                }
            }
        }
    }
}
