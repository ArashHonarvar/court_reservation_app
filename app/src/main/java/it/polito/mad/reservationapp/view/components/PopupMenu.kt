package it.polito.mad.reservationapp.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel

@Composable
fun PopupMenu(sportRatings: MutableList<Pair<MutableState<String>, MutableState<Float>>>, setPopupMenuDisplay: (Boolean) -> Unit, mainViewModel: AppViewModel = viewModel(),
              firebaseViewModel: FirebaseViewModel = viewModel()) {

    //val sports = mainViewModel.repo.getSports().observeAsState(initial = emptyList())

    val sports by remember { firebaseViewModel.getSports() }.observeAsState()

    val (selectedItem, setSelectedItem) = remember {
        mutableStateOf( Pair("0","No sports") )
    }

    LaunchedEffect(sports){
        if(sports?.getOrDefault(emptyList())?.isNotEmpty() == true) {
            val newPairs: MutableList<Pair<String, String>> = sports!!.getOrDefault(emptyList())
                ?.mapIndexed { index, element ->
                    (index + 1).toString() to element
                }?.toMutableList() ?: mutableListOf()

            setSelectedItem(
                newPairs
                    .toMap()
                    .filter { entry -> sportRatings.none { it.first.value == entry.value } }
                    .entries.firstOrNull()?.toPair()?:Pair("0","No sports")
            )
        }
    }

    AlertDialog(
        confirmButton = {
            CustomButton(
            text = " Add ",
        ) {
            if (selectedItem.second != "No sports") {
                sportRatings.add(Pair(mutableStateOf(selectedItem.second), mutableStateOf(0f)))
            }
            setPopupMenuDisplay(false)
            setSelectedItem(Pair<String,String>("0","No sports"))

        }},
        dismissButton = {
            CustomButton(
            text = "Close",
        ) {
            setPopupMenuDisplay(false)
            setSelectedItem(Pair<String,String>("0","No sports"))
        }},
        onDismissRequest = {
            setPopupMenuDisplay(false)
            setSelectedItem(Pair<String,String>("0","No sports"))
        },
        text = {
                Column(Modifier.padding(horizontal = 0.dp)) {
                    Text(
                        text = "Select a new sport",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )

                    val pairs: MutableList<Pair<String, String>> = sports?.getOrDefault(emptyList())
                        ?.mapIndexed { index, element ->
                            (index + 1).toString() to element
                        }?.toMutableList() ?: mutableListOf()

                    DropdownMenuDemo(
                        items = pairs.toMap().filter { entry ->
                            sportRatings.none { it.first.value == entry.value }
                        },
                        selectedItem,
                        setSelectedString = setSelectedItem
                    )
                }
        }
    )
}