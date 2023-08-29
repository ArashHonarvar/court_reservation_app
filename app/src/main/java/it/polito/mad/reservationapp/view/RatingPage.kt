package it.polito.mad.reservationapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.EditableRatingBarRow
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.viewModel.AppViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import it.polito.mad.reservationapp.model.firebase.Review
import it.polito.mad.reservationapp.ui.theme.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingPage(
    courtID: String,
    reservationID: String,
    navController: NavController,
    mainViewModel: AppViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    val court by remember { firebaseViewModel.getCourtById(courtID) }.observeAsState()
    //val court = mainViewModel.repo.getCourtByID(courtID.toInt()).observeAsState()

    //val reviewDB by remember { firebaseViewModel.getReviewByCourtId(courtID) }.observeAsState()
    //val reviewDB = mainViewModel.repo.getReviewByCourtID(courtID.toInt()).observeAsState()

    val (qualityRating, setQualityRating) = remember {
        mutableStateOf(0F)
    }
    val (facilityRating, setFacilityRating) = remember {
        mutableStateOf(0F)
    }
    val (ratingComment, setRatingComment) = remember {
        mutableStateOf("")
    }

//    LaunchedEffect(reviewDB.value) {
//        setQualityRating(reviewDB.value?.qualityRating ?: 0F)
//        setFacilityRating(reviewDB.value?.facilityRating ?: 0F)
//    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Column(Modifier.padding(16.dp)) {
            PageTitle(title = "Give us your opinion!", Modifier.align(Alignment.CenterHorizontally))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = court?.getOrNull()?.sport_name ?: "Sport",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = court?.getOrNull()?.name ?: "Court",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(0.dp)
                    ) {

                        EditableRatingBarRow(
                            text = "Quality",
                            rating = qualityRating,
                            setRating = setQualityRating,
                            onCancel = null
                        ) { newRating ->
                            setQualityRating(newRating)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(0.dp)
                    ) {

                        EditableRatingBarRow(
                            text = "Facility",
                            rating = facilityRating,
                            setRating = setFacilityRating,
                            onCancel = null,
                        ) { newRating ->
                            setFacilityRating(newRating)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        OutlinedTextField(
                            value = ratingComment,
                            onValueChange = { setRatingComment(it) },
                            label = { Text("Comment") },
                            readOnly = false,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.5f
                                ),
                                focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        )
                    }
                }
            }

            // Second row
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(text = "Submit", modifier = Modifier.weight(1f), onClick = {
                    firebaseViewModel.saveReviewForReservation(reservationID, Review(qualityRating, facilityRating, ratingComment) )
                    navController.navigate("HomePage")
                })
            }
        }
    }
}