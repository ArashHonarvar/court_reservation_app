package it.polito.mad.reservationapp.model.firebase.service

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.mad.reservationapp.model.firebase.Achievement
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.utils.toAchievement
import it.polito.mad.reservationapp.utils.toInterestedSport
import it.polito.mad.reservationapp.utils.toUser
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class UserServiceImpl: UserService {
    private val _db = Firebase.firestore
    private val _storage = Firebase.storage
    private val TAG = "FirebaseUserService"

    private lateinit var _userListener: ListenerRegistration

    override suspend fun getUserByIdRealTime(userId: String, cb: (User) -> Unit) {
        _userListener = _db.collection("users").document(userId)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
//                    Log.d(TAG, "Current data: ${value.data}")
                    cb(value.toUser())
                } else {
//                    Log.d(TAG, "Current data: null")
                }
            }
    }

    override fun detachUserListener() {
        _userListener.remove()
    }

    override suspend fun getUserById(userId: String): User? {
        val user = _db.collection("users").document(userId).get().await()
        if (user != null)
            return user.toUser()
        return null
    }

    override fun updateUser(user: User) {
        _db.collection("users").document(user.id!!).set(user.toHashMap())
            .addOnSuccessListener {
                Log.d(TAG, "User updated")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failure on updating user")
            }
    }

    override suspend fun createUserIfNotExists(user: User) {
        val ref = _db.collection("users").document(user.id ?: "null")
        if (!ref.get().await().exists()) {
            _db.collection("users").document(user.id!!).set(user.toHashMap())
                .addOnSuccessListener {
                    Log.d(TAG, "SUCCESS: User created")
                }
                .addOnFailureListener {
                    Log.d(TAG, "FAILURE: User not created")
                }
        }
    }

    override suspend fun getInterestedSportsByUserId(userId: String): List<InterestedSport> {
        val interestedSports = mutableListOf<InterestedSport>()

        val userSports = _db.collection("users").document(userId).collection("interested_sports").get().await()

        for (doc in userSports) {
            val achievements = mutableListOf<Achievement>()
            val achievementResult = _db.collection("users")
                .document(userId)
                .collection("interested_sports")
                .document(doc.id)
                .collection("achievements")
                .get().await()

            for (d in achievementResult) {
                achievements.add(d.toAchievement())
            }
            interestedSports.add(doc.toInterestedSport(achievements))
        }

        return interestedSports
    }

    override suspend fun saveInterestedSportsByUserId(userId: String, interestedSports: List<InterestedSport>) {
        val user = _db.collection("users")
            .document(userId)
            .get()
            .await()

        if (user != null) {
            val newInterestedSports = user.toUser().apply { this.interested_sports = interestedSports }
            _db.collection("users").document(userId).set(newInterestedSports)
        } else {
            Log.e(TAG, "Error saving review for the reservation")
        }
    }

    override suspend fun getProfileImage(userId: String): ByteArray {
        val image = _storage.reference.child("${userId}/profile.png")
            .getBytes(Long.MAX_VALUE)
            .await()
        return image
    }

    override suspend fun getProfileImageByName(userId: String, name: String): ByteArray {
        val image = _storage.reference.child("${userId}/${name}")
            .getBytes(Long.MAX_VALUE)
            .await()
        return image
    }

    override fun saveProfilePicture(userId: String, filename: String, data: ByteArray) {
        _storage.reference.child("${userId}/${filename}").putBytes(data)
    }

    override suspend fun getProfileImageReference(userId: String): String? {
        val ref = _storage.reference.child(userId)
        val files = ref.listAll().await()
        return files.items.map { it.name }.sorted().lastOrNull()
    }
}