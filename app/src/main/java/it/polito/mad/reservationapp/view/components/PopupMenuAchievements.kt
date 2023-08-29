package it.polito.mad.reservationapp.view.components


import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import it.polito.mad.reservationapp.model.firebase.Achievement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupMenuAchievements(
    sportRatings: MutableList<Pair<MutableState<String>, MutableState<Float>>>,
    achievements: SnapshotStateMap<String, List<Achievement>?>,
    setPopupMenuAchievementsDisplay: (Boolean) -> Unit
) {


    val sports: List<MutableState<String>> = sportRatings.map { it.first }
    val (title, setTitle) = remember { mutableStateOf("") }
    val (description, setDescription) = remember { mutableStateOf("") }

    val (selectedSport, setSelectedSport) = remember {
        mutableStateOf(Pair("0", "No sports"))
    }
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.datePicker.maxDate = Date().time

    LaunchedEffect(sports) {
        if (sports.isNotEmpty()) {
            val newPairs: MutableList<Pair<String, String>> = sports.mapIndexed { index, element ->
                (index + 1).toString() to element.value
            }.toMutableList()

            setSelectedSport(
                newPairs
                    .toMap()
                    .entries.firstOrNull()?.toPair() ?: Pair("0", "No sports")
            )
        }
    }

    AlertDialog(
        onDismissRequest = {
            setPopupMenuAchievementsDisplay(false)
            setSelectedSport(Pair("0", "No sports"))
        },
//        title = {
//            Text(
//                text = "Select a sport",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp,
//                modifier = Modifier.padding(10.dp)
//            )
//        },
        text = {
            LazyColumn(Modifier.padding(horizontal = 0.dp)) {
                item {
                    val pairs: MutableList<Pair<String, String>> =
                        sports.mapIndexed { index, element ->
                            (index + 1).toString() to element.value
                        }.toMutableList()

                    Row() {
                        Text(
                            text = "Select a sport",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Row(modifier = Modifier.padding(7.dp)) {
                        DropdownMenuDemo(
                            items = pairs.toMap(),
                            selectedSport,
                            setSelectedString = setSelectedSport
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 100.dp) // Set maximum height constraint
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Displaying the mDate value in the Row
                            TextField(
                                shape = RoundedCornerShape(4.dp),
                                value = mDate.value,
                                onValueChange = { newValue ->
                                    // Validate the input to allow only date values
                                    mDate.value = newValue
                                },
                                label = { Text("Select a date") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            mDatePickerDialog.show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.background,
                                        )
                                    }
                                },
                                readOnly = true,
                                enabled = false,
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    cursorColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTextColor = Color.Black,
                                    containerColor = Color.White,
                                    disabledLabelColor = Color.Black,
                                    disabledSupportingTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f) //
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 100.dp) // Set maximum height constraint
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { setTitle(it) },
                                label = { Text("Title") },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.Black,
                                    cursorColor = MaterialTheme.colorScheme.onSurface,
                                    textColor = Color.Black,
                                    containerColor = Color.White
                                ),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f) // Use weight to take up available space
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 200.dp) // Set maximum height constraint
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { setDescription(it) },
                                label = { Text("Description") },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.Black,
                                    cursorColor = MaterialTheme.colorScheme.onSurface,
                                    textColor = Color.Black,
                                    containerColor = Color.White
                                ),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f) // Use weight to take up available space
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            CustomButton(
                text = " Add ",
                onClick = {
                    // Handle confirm button click
                    if (mDate.value == "") {
                        Toast.makeText(
                            mContext,
                            "Please select a date for the achievement",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (title == "") {
                        Toast.makeText(
                            mContext,
                            "Please enter a title for the achievement",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (description == "") {
                        Toast.makeText(
                            mContext,
                            "Please enter a description for the achievement",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val sportName = selectedSport.second
                        val dateFormat =
                            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val date: Date = dateFormat.parse(mDate.value)
                        val achievement: Achievement =
                            Achievement(date, title, description)
                        achievements.compute(sportName) { _, existingList ->
                            val updatedList =
                                existingList?.toMutableList() ?: mutableListOf()
                            updatedList.add(achievement)
                            updatedList
                        }
                        setPopupMenuAchievementsDisplay(false)
                    }

                },
            )
        },
        dismissButton = {
            CustomButton(
                text = "Close",
                onClick = {
                    // Handle dismiss button click
                    setPopupMenuAchievementsDisplay(false)
                    setSelectedSport(Pair<String, String>("0", "No sports"))
                },
            )
        },
        modifier = Modifier.wrapContentSize()
    )

}
