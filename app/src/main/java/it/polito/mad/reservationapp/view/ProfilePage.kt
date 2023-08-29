package it.polito.mad.reservationapp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import it.polito.mad.reservationapp.MainActivity
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.view.components.EditableRatingBarRow
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.PopupMenu
import it.polito.mad.reservationapp.view.components.RatingBarRow
import it.polito.mad.reservationapp.view.components.SportDisplayer
import it.polito.mad.reservationapp.model.firebase.Achievement
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.ui.theme.Background
import it.polito.mad.reservationapp.ui.theme.Background_Secondary
import it.polito.mad.reservationapp.view.components.PopupMenuAchievements
import it.polito.mad.reservationapp.view.components.CustomButton
import it.polito.mad.reservationapp.view.components.DeleteConfirmationAlert
import it.polito.mad.reservationapp.viewModel.UserViewModel
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    navController: NavController,
    context: MainActivity,
    profileViewModel: UserViewModel,
    setBottomBarState: (Boolean)->Unit
) {

    /* --- From Firebase --- */
    val user by remember { profileViewModel.user }.observeAsState()
    val profileImageRepo by remember { profileViewModel.profileImage}.observeAsState()

    /* --- Local state --- */
    val selectedTabIndex = remember { mutableStateOf(0) }
    val (profileImage, setProfileImage) = remember {
        mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.myavatar21).asImageBitmap())
    }
    val (fullName, setFullName) = remember { mutableStateOf("") }
    val (nickname, setNickname) = remember { mutableStateOf("") }
    val (phoneNumber, setPhoneNumber) = remember { mutableStateOf("") }
    val (age, setAge) = remember { mutableStateOf(-1) }
    val (city, setCity) = remember { mutableStateOf("") }
    val (bio, setBio) = remember { mutableStateOf("") }
    val sportRatings = remember { mutableStateListOf<Pair<MutableState<String>, MutableState<Float>>>() }
    val achievements = remember { mutableStateMapOf<String, List<Achievement>?>() }
    val (isEdit, setIsEdit) = remember { mutableStateOf(false) }
    val (popupMenuDisplay, setPopupMenuDisplay) = remember { mutableStateOf(false) }
    val (popupMenuAchievementsDisplay, setPopupMenuAchievementsDisplay) = remember {
        mutableStateOf(
            false
        )
    }
    val (selectedMenuItem, setSelectedMenuItem) = remember { mutableStateOf("") }
    val (profileImageUri, setProfileImageUri) = remember { mutableStateOf<Uri?>(null) }

    val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    val (deleteAchievementAlertDisplay, setDeleteAchievementAlertDisplay) = remember { mutableStateOf(false) }
    val (deleteSportName, setDeleteSportName) = remember { mutableStateOf("") }
    val (deleteTitle, setDeleteTitle) = remember { mutableStateOf("") }
    val (deleteInterestAlertDisplay, setDeleteInterestAlertDisplay) = remember { mutableStateOf(false) }

    val menuItems = listOf(
        "Take a picture from camera",
        "Select a picture from gallery"
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
            // save the bitmap to file and set it as the profile image
            if (isSaved) {
                val bitmap = profileImageUri?.let { uriToBitmap(context, it) }
                if(bitmap != null){
                    profileViewModel.uploadProfilePicture(user?.id,bitmap)
                    setProfileImage(bitmap.asImageBitmap())
                }

            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // convert the uri to a bitmap, save it to file, and set it as the profile image
            val bitmap = uri?.let { uriToBitmap(context, it) }
            if (bitmap != null) {
                profileViewModel.uploadProfilePicture(user?.id,bitmap)
                setProfileImage(bitmap.asImageBitmap())
            }
        }

    LaunchedEffect(profileImageRepo){
        if(profileImageRepo!=null)
            setProfileImage(profileImageRepo!!)
    }

    LaunchedEffect(user) {
        if (user!= null) {
            setFullName(user!!.full_name?:"")
            setNickname(user!!.nickname ?: "")
            setAge(user!!.age ?: 0)
            setCity(user!!.city ?: "")
            setBio(user!!.bio ?: "")
            user!!.phone_number?.let { setPhoneNumber(it) }
            sportRatings.clear()
            if (user!!.interested_sports != null) {
                sportRatings.addAll(user!!.interested_sports!!.map {
                    Pair(
                        mutableStateOf(it.sport_name),
                        mutableStateOf(it.level)
                    )
                })

                user!!.interested_sports!!.forEach {
                    achievements[it.sport_name] = it.achievements
                }
            }
        }
    }

    val handleUpdateUser: ()->Unit = {
        profileViewModel.updateUser(
            User(
                user!!.id,
                fullName,
                city,
                nickname,
                age,
                bio,
                phoneNumber,
                sportRatings.toInterestedSportsList(achievements.toMap())
            )
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column() {
            Row() {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PageTitle(title = "My Profile")
                        Button(
                            onClick = {
                                if (isEdit && user!=null) {
                                    handleUpdateUser()
                                }
                                setIsEdit(!isEdit)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = if (isEdit) "Save " else "Edit ",
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Icon(
                                if (isEdit) Icons.Default.Done else Icons.Default.Edit,
                                contentDescription = "Icona modifica",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Row(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                                    text = "Info",
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
                                    text = "Interests",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            },
                            modifier = Modifier.background(Background_Secondary),
                            unselectedContentColor = Color.White,
                            selectedContentColor = it.polito.mad.reservationapp.ui.theme.Button
                        )

                        Tab(
                            selected = selectedTabIndex.value == 2,
                            onClick = {
                                selectedTabIndex.value = 2
                            },
                            text = {
                                Text(
                                    text = "Achievements",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            },
                            modifier = Modifier
                                .background(Background_Secondary)
                                .padding(0.dp),
                            unselectedContentColor = Color.White,
                            selectedContentColor = it.polito.mad.reservationapp.ui.theme.Button
                        )
                    }


                    if (selectedTabIndex.value == 0) {
                        //Info Tab
                        LazyColumn(modifier = Modifier) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(220.dp)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.TopEnd // Aligns the IconButton to the top end of the Box
                                    ) {
                                        Image(
                                            bitmap = profileImage.cropToSquare(),
                                            contentDescription = "Profile Picture",
                                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (isEdit) {
                                            IconButton(
                                                onClick = { setSelectedMenuItem(menuItems[0]) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Modify",
                                                    tint = Color.Black
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = selectedMenuItem != "",
                                            onDismissRequest = { setSelectedMenuItem("") },
                                        ) {
                                            menuItems.forEach { menuItem ->
                                                DropdownMenuItem(
                                                    text = { Text(menuItem) },
                                                    onClick = {
                                                        setSelectedMenuItem(menuItem)
                                                        if (menuItem == "Take a picture from camera") {

                                                            if (checkSelfPermission(
                                                                    context,
                                                                    Manifest.permission.CAMERA
                                                                ) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                                                                    context,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                                )
                                                                == PackageManager.PERMISSION_DENIED
                                                            ) {
                                                                val permission = arrayOf(
                                                                    Manifest.permission.CAMERA,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                                )
                                                                requestPermissions(
                                                                    context,
                                                                    permission,
                                                                    121
                                                                )
                                                            } else {
                                                                val file = createImageFile(context = context)
                                                                val uri = FileProvider.getUriForFile(
                                                                    context,
                                                                    "it.polito.mad.reservationapp.provider",
                                                                    file
                                                                )
                                                                setProfileImageUri(uri)
                                                                cameraLauncher.launch(uri)
                                                            }
                                                        } else if (menuItem == "Select a picture from gallery") {
                                                            galleryLauncher.launch("image/*")
                                                        }
                                                        setIsEdit(true)
                                                        setSelectedMenuItem("")
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                OutlinedTextField(
                                    value = bio,
                                    onValueChange = { setBio(it) },
                                    label = { Text("Bio") },
                                    readOnly = !isEdit,
                                    textStyle = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Gray,
                                        cursorColor = MaterialTheme.colorScheme.primary,
                                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.5f
                                        )
                                    ),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { setFullName(it) },
                                    label = { Text("Name") },
                                    readOnly = !isEdit,
                                    textStyle = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        //backgroundColor = Color(0xFF2D2D2D),
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
                                        .wrapContentWidth()
                                )

                                OutlinedTextField(
                                    value = nickname,
                                    onValueChange = { setNickname(it) },
                                    label = { Text("Username") },
                                    readOnly = !isEdit,
                                    textStyle = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Light
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        //backgroundColor = Color(0xFF2D2D2D),
                                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(
                                            alpha = 0.5f
                                        )
                                    ),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .wrapContentWidth()
                                )

                                Column(modifier = Modifier.padding(8.dp)) {
                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = {
                                                newValue -> if(newValue.isNotEmpty() && !newValue.startsWith("+") && !newValue.startsWith("0") && !newValue.startsWith("00")){
                                            Toast.makeText(
                                                context,
                                                "Please enter your phoneNumber with prefix ( + or 00 )",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }else{
                                            setPhoneNumber(newValue)
                                        }
                                                        },
                                        label = { Text("PhoneNumber") },
                                        readOnly = !isEdit,
                                        textStyle = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Light
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                alpha = 0.5f
                                            )
                                        ),
                                        modifier = Modifier
                                            .wrapContentWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                        ),
                                    )
                                    Text(
                                        text = "Enter your phone number with the prefix ( + or 00 )",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {

                                    OutlinedTextField(
                                        value = "$age",
                                        onValueChange = { if (it != "") setAge(it.filter { value ->  value.isDigit() }.toIntOrNull() ?: 0) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = { Text("Age") },
                                        readOnly = !isEdit,
                                        textStyle = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Light
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            //backgroundColor = Color(0xFF2D2D2D),
                                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                alpha = 0.5f
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(0.4f)
                                    )

                                    OutlinedTextField(
                                        value = city,
                                        onValueChange = { setCity(it) },
                                        label = { Text("Location") },
                                        readOnly = !isEdit,
                                        textStyle = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraLight
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            //backgroundColor = Color(0xFF2D2D2D),
                                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                alpha = 0.5f
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(0.6f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    CustomButton(text = "Logout") {
                                        profileViewModel.signOut(context)
                                        setBottomBarState(false)
                                        navController.navigate("SignInPage")
                                    }
                                }

                            }
                        }
                    } else if (selectedTabIndex.value == 1) {

                        if (isEdit) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                CustomButton(text = "+ New Interest", onClick = {
                                    setPopupMenuDisplay(true)
                                })
                            }
                        }

                        if (popupMenuDisplay) {
                            PopupMenu(sportRatings = sportRatings, setPopupMenuDisplay)
                        }
                        //Interests Tab
                        LazyColumn(modifier = Modifier.padding(5.dp)) {
                            item {
                                sportRatings.sortWith { pair, pair2 ->
                                    if (pair.first.value < pair2.first.value)
                                        -1
                                    else
                                        1
                                }
                                sportRatings.forEachIndexed { index, pair ->
                                    if (isEdit) {
                                        EditableRatingBarRow(
                                            text = pair.first.value,
                                            rating = pair.second.value,
                                            setRating = {},
                                            onCancel = {
                                                setDeleteInterestAlertDisplay(true)
                                            }
                                        ) { newRating ->
                                            sportRatings[index].second.value = newRating
                                        }
                                        if(deleteInterestAlertDisplay){
                                            DeleteConfirmationAlert(setOpenDialog = setDeleteInterestAlertDisplay, onConfirm = {sportRatings.removeAt(index)})
                                        }
                                    } else {
                                        RatingBarRow(
                                            text = pair.first.value,
                                            rating = pair.second.value
                                        )
                                    }
                                }
                            }
                        }
                    } else if (selectedTabIndex.value == 2) {
                        if(deleteAchievementAlertDisplay) {
                            DeleteConfirmationAlert(
                                setOpenDialog = setDeleteAchievementAlertDisplay,
                                onConfirm = {
                                    achievements.compute(deleteSportName) { _, existingList ->
                                        val updatedList = existingList?.toMutableList() ?: mutableListOf()
                                        updatedList.removeAll { i -> (i.title == deleteTitle) }
                                        updatedList
                                    }
                                }
                            )
                        }


                        if (isEdit) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                CustomButton(text = "+ New Achievement", onClick = {
                                    setPopupMenuAchievementsDisplay(true)
                                })
                            }
                        }

                        if (popupMenuAchievementsDisplay) {
                            PopupMenuAchievements(
                                sportRatings = sportRatings,
                                achievements = achievements,
                                setPopupMenuAchievementsDisplay
                            )
                        }

                        //Achievements Tab
                        LazyColumn(modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp, top = 5.dp)) {
                            item {

                                achievements.keys.forEach {sportName ->
                                    if (achievements[sportName] != null && achievements[sportName]?.isNotEmpty() == true)
                                        SportDisplayer(sport = sportName)

                                    achievements[sportName]?.forEach {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            Card(modifier = Modifier) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = df.format(inputDateFormat.parse(it.date.toString())),
                                                            textAlign = TextAlign.Start,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier
                                                                .padding(10.dp)
                                                        )
                                                    }

                                                    if(isEdit) {
                                                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                                            Icon(
                                                                imageVector = Icons.Default.Delete,
                                                                contentDescription = "Delete",
                                                                tint = Color.Red,
                                                                modifier = Modifier
                                                                    .padding(8.dp)
                                                                    .clickable(
                                                                        onClick =
                                                                        {
                                                                            setDeleteAchievementAlertDisplay(
                                                                                true
                                                                            )
                                                                            setDeleteTitle(it.title)
                                                                            setDeleteSportName(
                                                                                sportName
                                                                            )
                                                                        }
                                                                    )
                                                            )
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = it.title,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(10.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = it.description,
                                                    modifier = Modifier.padding(10.dp),
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(modifier = Modifier) {
                                            Divider(
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.height(2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }

                        }

                    }

                }
            }
        }
    }


}

private fun uriToBitmap(context: MainActivity, selectedFileUri: Uri): Bitmap? {
    try {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun createImageFile(context: Context): File {
    // Create an image file name
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "profile", //prefix
        ".png", //suffix
        storageDir //directory
    )
}

fun ImageBitmap.cropToSquare(): ImageBitmap {
    val size = Integer.min(width, height)
    val x = (width - size) / 2
    val y = (height - size) / 2
    return Bitmap.createBitmap(this.asAndroidBitmap(), x, y, size, size).asImageBitmap()
}

fun SnapshotStateList<Pair<MutableState<String>, MutableState<Float>>>.toInterestedSportsList(
    achievements: Map<String, List<Achievement>?>
): List<InterestedSport> {
    return this.toList().map {
        InterestedSport(it.first.value, it.second.value, achievements[it.first.value]?: emptyList())
    }
}

