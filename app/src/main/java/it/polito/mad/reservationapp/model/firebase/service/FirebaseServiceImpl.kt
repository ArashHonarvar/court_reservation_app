package it.polito.mad.reservationapp.model.firebase.service

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.mad.reservationapp.model.firebase.Achievement
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.model.firebase.CardHighlights
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.Review
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.utils.toAchievement
import it.polito.mad.reservationapp.utils.toAnnouncement
import it.polito.mad.reservationapp.utils.toCourt
import it.polito.mad.reservationapp.utils.toInterestedSport
import it.polito.mad.reservationapp.utils.toReservation
import it.polito.mad.reservationapp.utils.toUser
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

@Singleton
class FirebaseServiceImpl: FirebaseService {
    private lateinit var userAnnouncementListener: ListenerRegistration
    private val TAG = "FirebaseAppService"
    private val _db = Firebase.firestore

    private val storage = Firebase.storage

    override suspend fun getCourtByReservationId(resId: String): Result<Court?> {
        var court: Court? = null

        val result = _db
            .collection("reservations")
            .document(resId).get().await()

        if (result != null) {
            court = result.getDocumentReference("court")?.get()?.await()?.toCourt()
        }

        return Result.success(court)
    }


    override fun saveReviewForReservation(reservationId: String, review: Review) {
        try{
            _db.collection("reservations").document(reservationId).update("review",review.toHashMap())
        }
        catch (e: Exception) {
            Log.e(TAG, "Error updating the review", e)
        }
    }

    override suspend fun getCourts(): Result<List<Court>?> {
        val courts = mutableListOf<Court>()
        val resultList = _db
            .collection("courts")
            .get().await()

        for (document in resultList) {
            courts.add(document.toCourt())
        }

        return Result.success(courts)
    }

    override suspend fun getCourtById(courtId: String): Result<Court?> {
        var court: Court? = null

        val result = _db
            .collection("courts")
            .document(courtId).get().await()

        if (result != null) {
            court = result.toCourt()
        }

        return Result.success(court)
    }

    override suspend fun getCourtsBySportName(sport_name: String): Result<List<Court>?> {
        val courts = mutableListOf<Court>()

        val resultList = _db
            .collection("courts")
            .whereEqualTo("sport_name", sport_name)
            .get().await()

        for (document in resultList) {
            courts.add(document.toCourt())
        }

        return Result.success(courts)
    }

    override suspend fun getReviewByCourtId(courtId: String): Result<Float> {
        val courtRef = _db.collection("courts").document(courtId)

        return suspendCoroutine { continuation ->
            _db.collection("reservations")
                .whereEqualTo("court", courtRef)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (!querySnapshot.isEmpty) {
                            val reviews = querySnapshot.documents.mapNotNull { documentSnapshot ->
                                documentSnapshot.toReservation(null, null).review
                            }
                            if(reviews.isNotEmpty()) {
                                val qualityAvg = reviews.map { it.quality_rating }
                                    .average()
                                    .toFloat()

                                val facilityAvg = reviews.map { it.facility_rating }
                                    .average()
                                    .toFloat()

                                val result = ((qualityAvg + facilityAvg) / 2f * 2).roundToInt() / 2f
                                continuation.resume(Result.success(result))
                            }else{
                                continuation.resume(Result.success(0f))
                            }
                        } else {
                            continuation.resume(Result.failure(Exception("No reviews found")))
                        }
                    } else {
                        continuation.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override suspend fun getSports(): Result<List<String>?> {
        val sports = mutableListOf<String>()
        val resultList = _db.collection("courts").get().await()

        for (document in resultList) {
            sports.add(document.toCourt().sport_name)
        }

        return Result.success(sports.distinct().sorted())
    }

    override suspend fun getAvailableTimeslotByCourtIDAndDate(courtId: String, date: String): Result<List<String>?> {
        val bookedTimeslots = mutableListOf<String>()
        val courtRef = _db.collection("courts").document(courtId)

        val timeslots = _db.collection("courts")
                .document(courtId).get().await().toCourt().timeslots

        val res = if (date != "")
            _db
            .collection("reservations")
            .whereEqualTo("court", courtRef)
            .whereEqualTo("reserved_date", createTimestampFromString(date))
            .get().await()
        else
            emptyList()

        for (document in res) {
            val court = document.getDocumentReference("court")?.get()?.await()?.toCourt()
            val user = document.getDocumentReference("user")?.get()?.await()?.toUser()
            bookedTimeslots.addAll(document.toReservation(court, user).timeslots)
        }

        return Result.success(timeslots.subtract(bookedTimeslots.toSet()).toList())
    }

    override suspend fun getCoverImage(): ByteArray {
        return storage.reference.child("app-content/image-cover/main-cover.png")
            .getBytes(Long.MAX_VALUE)
            .await()
    }

    override suspend fun getCardImages(): List<CardHighlights> {
        val cards = mutableListOf<CardHighlights>()
        val ref = storage.reference.child("app-content/homepage-card")
        val list = ref.listAll().await()
        list.items.forEach { item ->
            val img = item.getBytes(Long.MAX_VALUE).await()
            cards.add(
                CardHighlights(
                    item.name,
                    BitmapFactory.decodeByteArray(img,0,img.size).asImageBitmap()
                )
            )
        }
        return cards
    }

    override suspend fun getHighlightsCardName(): List<String> {
        val cards = mutableListOf<String>()
        val ref = storage.reference.child("app-content/homepage-card")
        val list = ref.listAll().await()
        list.items.forEach {
            cards.add(it.name)
        }
        return cards
    }
}

fun createTimestampFromString(dateString: String): Date? {
    if (dateString == "")
        return null

    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
    val timestampFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val date: Date = try {
        dateFormat.parse(dateString)
    } catch (_: Exception) {
        timestampFormat.parse(dateString)
    }

    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}