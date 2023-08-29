package it.polito.mad.reservationapp.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.view.components.Indicator
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.StartupView
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import it.polito.mad.reservationapp.viewModel.HomePageViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage(
    navController: NavController,
    context: Context,
    setBottomBarState: (Boolean)->Unit,
    isFirstStart: Boolean,
    setIsFirstStart: (Boolean) -> Unit,
    homePageViewModel: HomePageViewModel,
) {
    val noImage = BitmapFactory.decodeResource(context.resources, R.drawable.noimage).asImageBitmap()
    val coverImage = homePageViewModel.coverImage.observeAsState()
    val cardImages = homePageViewModel.cardImages.observeAsState()

    val (isLoaded, setIsLoaded) = remember { mutableStateOf(false) }
    val (showContent, setShowContent) = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = coverImage.value, key2 = cardImages.value){
        if(coverImage.value!=null && cardImages.value!=null){
            setIsLoaded(true)
            launch {
                delay(2000L)
                setShowContent(true)
                setBottomBarState(true)
                setIsFirstStart(false)
            }
        }
    }

    if(!showContent && isFirstStart)
        StartupView(isLoaded)
    else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Image with rounded corners
            if (!isLoaded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(shape = RoundedCornerShape(bottomEnd = 140.dp, bottomStart = 140.dp)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { Indicator() }
            } else {
                Image(
                    bitmap = coverImage.value ?: noImage,
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(shape = RoundedCornerShape(bottomEnd = 140.dp, bottomStart = 140.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Buttons forming a "V" shape at the bottom
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("AnnouncementsPage") },
                        modifier = Modifier
                            .offset(y = (-16).dp)
                            .size(80.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(it.polito.mad.reservationapp.ui.theme.Button)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Text(
                        text = "Announcements",
                        modifier = Modifier.offset(y = (-16).dp)
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("MyReservations") },
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = 30.dp, y = (-40).dp)
                            .size(80.dp),
                        colors = ButtonDefaults.buttonColors(it.polito.mad.reservationapp.ui.theme.Button)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Reservations",
                        modifier = Modifier.offset(x = 30.dp, y = (-40).dp)
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("AboutUsPage") },
                        modifier = Modifier
                            .offset(x = (-40).dp, y = (-40).dp)
                            .size(80.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(it.polito.mad.reservationapp.ui.theme.Button)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "About us",
                        modifier = Modifier.offset(x = (-40).dp, y = (-40).dp)
                    )
                }

            }

            PageTitle(
                title = "Highlights",
                Modifier
                    .align(Alignment.Start)
                    .padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            val state = rememberPagerState()
            val isDragged by state.interactionSource.collectIsDraggedAsState()
            val sportImage = remember { mutableStateOf(noImage) }
            Box(modifier = Modifier.fillMaxWidth()) {
                if (!isLoaded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { Indicator() }
                } else {
                    HorizontalPager(
                        state = state,
                        count = cardImages.value?.size ?: 0,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    ) { page ->
                        sportImage.value = cardImages.value?.get(page)?.bitmap ?: noImage
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.BottomCenter) {
                                Image(
                                    bitmap = sportImage.value,
                                    contentDescription = "", Modifier
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .fillMaxSize(), contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = cardImages.value?.get(page)?.title
                                        ?.replaceFirstChar { it.uppercase() }
                                        ?.substringBefore('.') ?: "",
                                    Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .padding(8.dp)
                                        .background(Color.LightGray.copy(alpha = 0.60F))
                                        .padding(8.dp),
                                    textAlign = TextAlign.Start,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.BottomCenter)//,
                    /*shape = CircleShape,
                color = Color.Gray.copy(alpha = 0.5f)*/
                ) {
                    DotsIndicator(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 15.dp)
                            .offset(y = (+35).dp),
                        totalDots = cardImages.value?.size ?: 0,
                        selectedIndex = if (isDragged) state.currentPage else state.targetPage,
                        dotSize = 8.dp
                    )
                }
            }
        }
    }
}
@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color.White,
    unSelectedColor: Color = Color.LightGray.copy(alpha = 0.60F) /* Color.Gray */,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}