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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.ui.theme.Button
import it.polito.mad.reservationapp.view.components.CourtWithRating
import it.polito.mad.reservationapp.view.components.DropdownMenuDemo
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.TimeslotDisplayer
import it.polito.mad.reservationapp.view.components.TimeslotFilterDisplayer
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import java.text.SimpleDateFormat

@Composable
fun CourtAvailabilityPage(sportName: String, date: String, navController: NavController, mainViewModel: AppViewModel = viewModel(), firebaseViewModel: FirebaseViewModel = viewModel()) {
    val df_in = SimpleDateFormat("yyyyMMdd")
    val df_out = SimpleDateFormat("dd/MM/yyyy")
    val parsedDate = df_out.format(df_in.parse(date))

    println("SPORTNAME $sportName")
    println("DATE $date")

    val (openFilter, setOpenFilter) = remember {
        mutableStateOf(false)
    }

    val (timeslots, setTimeslots) = remember {
        mutableStateOf(mapOf(Pair(0, "All")))
    }

    val courts by remember { firebaseViewModel.getCourtsBySportName(sportName) }.observeAsState()

    val dbReviews by remember {
        firebaseViewModel.getReviewsBySportName(sportName)
    }.observeAsState()

    val (reviews, setReviews) = remember {
        mutableStateOf(emptyMap<String, Float>())
    }

    LaunchedEffect(courts) {
        if (courts?.getOrNull() != null) {

            val reviewMap = mutableMapOf<String, Float>()

            setTimeslots(timeslots.plus(courts!!.getOrNull()!!.flatMap {
                it -> it.timeslots
            }.distinct().sorted().mapIndexed { index, element ->
                (index + 1) to element
            }.toMap()))
        }
    }

    LaunchedEffect(dbReviews) {
        if (dbReviews != null && dbReviews!!.isNotEmpty()) {
            val reviewMap = mutableMapOf<String, Float>()
            dbReviews!!.forEach {
                reviewMap[it.first] = it.second.getOrDefault(0f)
            }
            setReviews(reviewMap)
        }
    }

    val (selectedTimeslots, setSelectedTimeslots) = remember {
        mutableStateOf(mapOf(Pair(0, "All")))
    }

    val (selectedCourts, setSelectedCourts) = remember {
        mutableStateOf(mapOf(Pair("all", "All")))
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Background)) {

        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "Book a Court", Modifier.align(Alignment.CenterHorizontally))

            Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.filter_line_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { setOpenFilter(!openFilter) },
                    tint = Button,
                )
            }
            if(openFilter) {
                Row(modifier = Modifier.padding(top=8.dp)) {
                    Column(
                        modifier = Modifier.weight(0.5F)
                    ) {
                        Row(){
                            Text(text="Courts", modifier=Modifier.padding(bottom = 8.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = selectedCourts.containsKey("all"), onCheckedChange = {
                                if ((selectedCourts.containsKey("all"))) setSelectedCourts(
                                    selectedCourts.minus("all")
                                ) else setSelectedCourts(mapOf(Pair("all", "All")))
                            })
                            Text(text = "All")
                        }
                        courts?.getOrNull().orEmpty().map{ it.id to it.name }.toMap().forEach{
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    val currentKey= it.key
                                    val currentValue = it.value
                                    Checkbox(checked = selectedCourts.containsKey(currentKey) || selectedCourts.containsKey("all"),
                                        onCheckedChange = {
                                        if( selectedCourts.containsKey("all"))
                                            setSelectedCourts(selectedCourts.minus("all"))
                                        else if(selectedCourts.containsKey(currentKey))
                                            setSelectedCourts(selectedCourts.minus(currentKey))
                                        else
                                            setSelectedCourts(selectedCourts.plus(Pair(currentKey,currentValue)))
                                    })
                                    Text(text = currentValue)
                                }
                            }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column( modifier = Modifier.weight(0.5F)
                    ) {
                        Row(){
                            Text(text="Timeslots", modifier=Modifier.padding(bottom = 8.dp))
                        }
                        LazyColumn( // specify the number of columns
                            modifier = Modifier.weight(1F),
                            verticalArrangement = Arrangement.spacedBy(16.dp),

                        ) {
                            itemsIndexed(
                                (timeslots.values).toList()
                            )
                            { index, it ->
                                Box(
                                    Modifier
                                        .clickable {
                                            if (it == "All") {
                                                if (selectedTimeslots.values.contains(it))
                                                    setSelectedTimeslots(emptyMap())
                                                else
                                                    setSelectedTimeslots(mapOf(Pair(0, "All")))
                                            } else {
                                                if (selectedTimeslots.values.contains("All")) {
                                                    setSelectedTimeslots(selectedTimeslots.filter { t -> t.value != "All" })
                                                } else {
                                                    if (selectedTimeslots.values.contains(it)) {
                                                        setSelectedTimeslots(selectedTimeslots.filter { t -> t.value != it })
                                                    } else {
                                                        setSelectedTimeslots(
                                                            selectedTimeslots.plus(
                                                                Pair<Int, String>(
                                                                    index + 1,
                                                                    it
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                        }
                                ) {
                                    TimeslotFilterDisplayer(text = it, selectedTimeslots.values.contains(it) || selectedTimeslots.containsValue("All"))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = sportName,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                courts?.getOrNull().orEmpty().filter {
                    if(selectedCourts.containsKey("all")){ true }
                    else {selectedCourts.containsKey(it.id)}
                }.forEach { court ->
                    item {
                        val availableTimeslots =
                            firebaseViewModel.getAvailableTimeslotByCourtIDAndDate(
                                court.id,
                                date
                            ).observeAsState()


                        if (availableTimeslots.value?.getOrNull() != null) {
                            val availableTimeslotsList = availableTimeslots.value?.getOrDefault(
                                emptyList()
                            )
                            if (selectedTimeslots.values.contains("All") || availableTimeslotsList?.intersect(selectedTimeslots.values.toSet())?.size!! > 0) {
                                Row(Modifier.clickable { navController.navigate("NewReservation/${court.id}/${date}") }) {
                                    CourtWithRating(
                                        court.name,
                                        reviews[court.id] ?: 0f
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                Divider(
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .height(2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}