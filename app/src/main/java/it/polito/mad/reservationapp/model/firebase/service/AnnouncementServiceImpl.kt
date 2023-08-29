package it.polito.mad.reservationapp.model.firebase.service

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.utils.toAnnouncement
import it.polito.mad.reservationapp.utils.toUser
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class AnnouncementServiceImpl: AnnouncementService {
    private val TAG = "FirebaseAnnouncementService"
    private val _db = Firebase.firestore

    private lateinit var userAnnouncementListener: ListenerRegistration
    private var activeAnnouncementListener: ListenerRegistration? = null

    override suspend fun getActiveAnnouncements(): List<Announcement> {
        val now = Timestamp.now().toDate()
        val announcements = mutableListOf<Announcement>()
        val res = _db.collection("announcements")
            .whereGreaterThanOrEqualTo("expiration_date", now)
            .get()
            .await()

        for(doc in res.documents){
            val owner = doc.getDocumentReference("owner")?.get()?.await()?.toUser()
            announcements.add(doc.toAnnouncement(owner!!))
        }

        return announcements
    }

    override fun getActiveAnnouncementsRealTime(userId: String, cb: (List<Announcement>) -> Unit) {
        val now = Timestamp.now().toDate()
        val userRef = _db.collection("users").document(userId)
        activeAnnouncementListener = _db.collection("announcements")
            .whereGreaterThanOrEqualTo("expiration_date", now)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                val announcements = mutableListOf<Announcement>()
                for(d in value!!){
                    val owner = d.getDocumentReference("owner")
                    if(owner != null && owner != userRef){
                        owner.get().addOnSuccessListener { o ->
                            announcements.add(d.toAnnouncement(o.toUser()))
                            cb(announcements)
                        }
                    }
                }
            }
    }

    override fun detachActiveAnnouncementsListener() {
        if(activeAnnouncementListener != null)
            activeAnnouncementListener!!.remove()
    }
    override fun getAnnouncementsByUserIdRealTime(userId: String, cb: (List<Announcement>) -> Unit) {
        val userRef = _db.collection("users").document(userId)
        userAnnouncementListener = _db.collection("announcements")
            .whereEqualTo("owner",userRef)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                val announcements = mutableListOf<Announcement>()
                userRef.get().addOnSuccessListener{u ->
                    for (doc in value!!){
                        announcements.add(doc.toAnnouncement(u.toUser()))
                    }
                    cb(announcements)
                }
            }
    }

    override fun detachUserAnnouncementListener() {
        userAnnouncementListener.remove()
    }

    override fun saveAnnouncement(announcement: Announcement) {
        val userRef = _db.collection("users").document(announcement.owner.id!!)
        _db.collection("announcements")
            .add(announcement.toHashMap(userRef) )
            .addOnSuccessListener {
                Log.d(TAG, "Save Announcement SUCCESS")
            }
            .addOnFailureListener {
                Log.d(TAG, "Save Announcement FAILED")
            }
    }

    override fun deleteAnnouncementById(id: String) {
        _db.collection("announcements").document(id).delete()
            .addOnSuccessListener {
                Log.d("DELETE RESERVATION", "SUCCESS")
            }
            .addOnFailureListener {
                Log.d("DELETE RESERVATION", "FAILURE")
            }
    }
}