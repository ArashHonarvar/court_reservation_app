package it.polito.mad.reservationapp.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.model.firebase.service.AnnouncementServiceImpl
import it.polito.mad.reservationapp.model.firebase.service.FirebaseServiceImpl
import it.polito.mad.reservationapp.model.firebase.service.ReservationServiceImpl
import it.polito.mad.reservationapp.model.firebase.service.UserServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class UserViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var signInLauncher: ActivityResultLauncher<Intent>

    private val _firebaseUserService = UserServiceImpl()
    private val _firebaseReservationService = ReservationServiceImpl()
    private val _firebaseAnnouncementService = AnnouncementServiceImpl()

    private val _userId = MutableLiveData<String?>()
    val userId = _userId

    private val _profileImage = MutableLiveData<ImageBitmap?>()
    val profileImage = _profileImage

    private val _user = MutableLiveData<User?>()
    var user = _user

    private val _userReservations = MutableLiveData<List<Reservation>>()
    val userReservations = _userReservations

    private val _userAnnouncement = MutableLiveData<List<Announcement>>()
    val userAnnouncement = _userAnnouncement

    init{
        viewModelScope.launch {
            // Default user
            _firebaseUserService.getUserByIdRealTime("user1"){_user.value = it}
        }
    }

    /* --- USER AUTHENTICATION --- */
    fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun signOut(context: Context) {
        _userId.value = null
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(
                //context.mainExecutor
            ) {
                Log.d("Signed Out: ", "Successful")
                Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT)
                    .show()
                _firebaseUserService.detachUserListener()
                _firebaseReservationService.detachReservationsListener()
                _firebaseAnnouncementService.detachUserAnnouncementListener()
                _userReservations.value = emptyList()
            }
            .addOnFailureListener {
                _firebaseUserService.detachUserListener()
                _firebaseReservationService.detachReservationsListener()
                _firebaseAnnouncementService.detachUserAnnouncementListener()
                _userId.value = null
                _userReservations.value = emptyList()
            }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, context: Context) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            firebaseAuthWithGoogle(context, idToken, account)
        } catch (e: ApiException) {
            Log.w("Sign In Error", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(context: Context, idToken: String?, account: GoogleSignInAccount) {
        val auth: FirebaseAuth = Firebase.auth
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println(user)
                    if(user!=null){
                        createUserIfNotExists(User(user.uid,account.displayName,"","",0,"","" ,emptyList()))
                        loadUserData(context, user.uid)
                    }
                } else {
                    println("FAILED BIG TIME")
                }
            }
    }

    fun loadUserData(context: Context, userId: String){
        _userId.value = userId
        _firebaseUserService.detachUserListener()
       viewModelScope.launch {
           _firebaseUserService.getUserByIdRealTime(_userId.value!!){_user.value = it}
           _firebaseReservationService.getReservationsByUserIdRealTime(_userId.value!!){_userReservations.value = it}
           _firebaseAnnouncementService.getAnnouncementsByUserIdRealTime(_userId.value!!){_userAnnouncement.value = it}
           fetchProfileImage(context, _userId.value!!)
       }
    }

    /* --- USER GENERAL ROUTINES --- */
    suspend fun getUserById(userId: String): User? {
        return _firebaseUserService.getUserById(userId)
    }

    fun uploadProfilePicture(userId: String?, bitmap: Bitmap) {
        if(userId == null)
            return
        val timestamp = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        val filename = "profile-${timestamp}.png"

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        _firebaseUserService.saveProfilePicture(userId, filename, data)
        val img = BitmapFactory.decodeByteArray(data,0,data.size).asImageBitmap()
        _profileImage.value = img
    }

    fun getInterestedSportsByUserId(userId: String): LiveData<List<InterestedSport>> {
        return liveData(Dispatchers.IO) {
            val interestedSports = _firebaseUserService.getInterestedSportsByUserId(userId)
            emit(interestedSports)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _firebaseUserService.updateUser(user)
        }
    }

    /* --- PRIVATE FUNCTIONS --- */
    private fun createUserIfNotExists(user: User){
        viewModelScope.launch{
            _firebaseUserService.createUserIfNotExists(user)
        }
    }

    private fun createDirIfNotExists(context: Context, dirname: String): File{
        val dir = File(context.filesDir, dirname)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

    private fun saveBitmapToPNGFile(bitmap: Bitmap, dirname: String, filename: String, context: Context) {
        val dir = File(context.filesDir, dirname)
        val fileOutputStream = FileOutputStream(File(dir, filename))

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    private suspend fun fetchProfileImage(context: Context, userId: String){
        val dir = createDirIfNotExists(context, "profile-image")
        val profileImageReference = _firebaseUserService.getProfileImageReference(userId)
        val cachedProfileImage = dir.listFiles()

        if(cachedProfileImage?.map { it.name }?.contains(profileImageReference)==true){
            _profileImage.value = BitmapFactory.decodeFile(cachedProfileImage[0].absolutePath).asImageBitmap()
        }
        else{
            if(profileImageReference != null){
                // Download from firebase
                val image = _firebaseUserService.getProfileImageByName(_userId.value?:"",profileImageReference)
                saveBitmapToPNGFile(
                    BitmapFactory.decodeByteArray(image,0,image.size),
                    "profile-image",
                    profileImageReference,
                    context
                )
                _profileImage.value = BitmapFactory.decodeByteArray(image,0,image.size).asImageBitmap()
            }
        }
    }
    fun fetchUsersProfileImages(userIds: List<String>) : LiveData<Map<String,ImageBitmap>>{
        return liveData(Dispatchers.IO) {
            val profileImages = mutableMapOf<String,ImageBitmap>()
            userIds.distinct().forEach {
                val profileImageReference = _firebaseUserService.getProfileImageReference(it)
                if (profileImageReference != null) {
                    // Download from firebase
                    val image = _firebaseUserService.getProfileImageByName(
                        it ?: "",
                        profileImageReference
                    )
                    profileImages[it] =
                        BitmapFactory.decodeByteArray(image, 0, image.size).asImageBitmap()
                }
            }
            emit(profileImages)
        }
    }
}