package it.polito.mad.reservationapp.view

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.ui.theme.Background_Secondary
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DeleteConfirmationAlert
import it.polito.mad.reservationapp.view.components.InfoText
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.PopupMenu
import it.polito.mad.reservationapp.view.components.PopupMenuAnnouncements
import it.polito.mad.reservationapp.view.components.RatingBar
import it.polito.mad.reservationapp.viewModel.AnnouncementBoardViewModel
import it.polito.mad.reservationapp.viewModel.FirebaseViewModel
import it.polito.mad.reservationapp.viewModel.UserViewModel
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnnouncementsPage(announcementBoardViewModel: AnnouncementBoardViewModel, profileViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel = viewModel()) {
    val context = LocalContext.current
    val announcements by remember{announcementBoardViewModel.activeAnnouncements}.observeAsState(emptyList())
    val myAnnouncements by remember { profileViewModel.userAnnouncement }.observeAsState(emptyList())
    val myUserId by remember { profileViewModel.userId }.observeAsState()
    val profilePictures by remember(announcements){profileViewModel.fetchUsersProfileImages(announcements?.mapNotNull { it.owner.id } ?: emptyList())}.observeAsState()
    //val profilePicture by remember { mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.myavatar21).asImageBitmap()) }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val (popupMenuDisplay, setPopupMenuDisplay) = remember { mutableStateOf(false) }

    LaunchedEffect( Unit ){
        if(myUserId != null)
            announcementBoardViewModel.loadActiveAnnouncements(myUserId!!)
    }

        Column(Modifier.fillMaxSize()) {
            PageTitle(title = "Announcements", modifier = Modifier.padding(start=16.dp))
            TabRow(
                selectedTabIndex.value,
                modifier = Modifier,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                            .height(4.dp)
                            .clip(RoundedCornerShape(8.dp)) // clip modifier not working
                            .padding(horizontal = 28.dp)
                            .background(it.polito.mad.reservationapp.ui.theme.Button)
                    )
                },
            ) {
                Tab(
                    selected = selectedTabIndex.value == 0,
                    onClick = {
                        selectedTabIndex.value = 0
                    },
                    text = {
                        Text(
                            text = "Public Announcements",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    modifier = Modifier.background(Background_Secondary),
                    unselectedContentColor = Color.White,
                    selectedContentColor = it.polito.mad.reservationapp.ui.theme.Button
                )
                Tab(
                    selected = selectedTabIndex.value == 1,
                    onClick = {
                        selectedTabIndex.value = 1
                    },
                    text = {
                        Text(
                            text = "My \nAnnouncements",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    modifier = Modifier.background(Background_Secondary),
                    unselectedContentColor = Color.White,
                    selectedContentColor = it.polito.mad.reservationapp.ui.theme.Button
                )
            }

            if (selectedTabIndex.value == 1) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                    CustomButton(text = "+ New Announcement", onClick = {
                        setPopupMenuDisplay(true)
                    })
                }
                if (popupMenuDisplay && profileViewModel.user.value != null) {
                    PopupMenuAnnouncements(firebaseViewModel, announcementBoardViewModel,
                        profileViewModel.user.value!!, setPopupMenuDisplay)
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (selectedTabIndex.value == 0) {
                        items(announcements.sortedBy { it.expiration_date }) {
//                        items(announcements?.filter { it.owner.id != myUserId } ?: emptyList()) { it ->
                            AnnouncementCard(
                                it,
                                profilePictures?.get(it.owner.id) ?: ImageBitmap(1, 1),
//                                profilePicture ?: ImageBitmap(1, 1),
                                null
                            )
                        }
                    }
                    if (selectedTabIndex.value == 1) {
                        items(myAnnouncements.sortedBy { it.expiration_date }) { it ->
                            AnnouncementCard(
                                it,
                                profilePictures?.get(it.owner.id) ?: ImageBitmap(1, 1),
//                                profilePicture ?: ImageBitmap(1, 1),
                                announcementBoardViewModel
                            )
                        }
                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementCard(announcement: Announcement, imageBitmap: ImageBitmap, announcementBoardViewModel: AnnouncementBoardViewModel?) {
    val (deleteAlertDisplay, setDeleteAlertDisplay) = remember { mutableStateOf(false) }
    val df_out = SimpleDateFormat("dd MMM")
    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (announcement.expiration_date < Date()) Color.Transparent else Background_Secondary
        ),
        onClick = {setIsExpanded(!isExpanded)}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() // edit animation here
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = announcement.sport,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = (if (announcement.expiration_date < Date()) "Expired " else "Expires ") + df_out.format(inputDateFormat.parse(announcement.expiration_date.toString())!!),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.End
                    )
                }
            if(!isExpanded) {
                if(announcementBoardViewModel == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = announcement.owner.nickname!!,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                        )
                        RatingBar(
                            rating = if(announcement.owner.interested_sports?.filter { it.sport_name == announcement.sport }
                                    ?.isNotEmpty() == true) announcement.owner.interested_sports?.filter { it.sport_name == announcement.sport }!![0].level else 0F,
                            modifier = Modifier
                                .weight(1f)
                                .size(200.dp, 27.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }else{
                if(announcementBoardViewModel == null) {
                    Row() {
                        Column(modifier = Modifier.weight(0.4F)) {
                            Image(
                                bitmap = imageBitmap.cropToSquare(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(105.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.6F)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = announcement.owner.full_name!!,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingBar(
                                    rating = if(announcement.owner.interested_sports?.filter { it.sport_name == announcement.sport }
                                            ?.isNotEmpty() == true) announcement.owner.interested_sports?.filter { it.sport_name == announcement.sport }!![0].level else 0F,
                                    modifier = Modifier
                                        .weight(1f)
                                        .size(200.dp, 27.dp),
                                )
                            }
                        }
                    }
                }
                Row() {
                    InfoText(text = announcement.info)
                }
                if(deleteAlertDisplay){
                    DeleteConfirmationAlert(setOpenDialog = setDeleteAlertDisplay, onConfirm = {
                        announcementBoardViewModel?.deleteAnnouncementById(announcement.id)
                    })
                }
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    if(announcementBoardViewModel != null) {
                        Button(onClick = {
                            setDeleteAlertDisplay(true)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.Red,
                            )
                            Text(
                                text = "Delete",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Red
                            )
                        }
                    }else {
                        Button(onClick = {
                            openPhoneApp(
                                context,
                                announcement.owner.phone_number.toString()
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = "Contact me!",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
fun openPhoneApp(context: Context, phoneNumber: String) {
    val phone = "tel:$phoneNumber"
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse(phone)
    }
    startActivity(context, intent, null)
}
