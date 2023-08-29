package it.polito.mad.reservationapp.model.firebase.service

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.utils.toCourt
import it.polito.mad.reservationapp.utils.toReservation
import it.polito.mad.reservationapp.utils.toUser
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class ReservationServiceImpl: ReservationService {
    private val _db = Firebase.firestore
    private val TAG = "FirebaseReservationService"

    private lateinit var userReservationsListener: ListenerRegistration

    override suspend fun getAllReservations(): List<Reservation> {
        val reservations = mutableListOf<Reservation>()
        val resultList = _db
            .collection("reservations")
            .get().await()

        for (document in resultList) {
            val court = document.getDocumentReference("court")?.get()?.await()?.toCourt()
            val user = document.getDocumentReference("user")?.get()?.await()?.toUser()
            reservations.add(document.toReservation(court, user))
        }

        return reservations
    }

    override suspend fun getReservationsByUserId(userId: String): List<Reservation> {
        val reservations = mutableListOf<Reservation>()
        val userRef = _db.collection("users").document(userId)
        val resultList = _db
            .collection("reservations")
            .whereEqualTo("user", userRef)
            .get().await()

        for (document in resultList) {
            val court = document.getDocumentReference("court")?.get()?.await()?.toCourt()
            val user = document.getDocumentReference("user")?.get()?.await()?.toUser()
            reservations.add(document.toReservation(court, user))
        }
        return reservations
    }

    override suspend fun getReservationsByUserIdRealTime(userId: String, cb: (List<Reservation>) -> Unit) {
        val userRef = _db.collection("users").document(userId)
        userReservationsListener = _db.collection("reservations").whereEqualTo("user", userRef)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val reservations = mutableListOf<Reservation>()
                for (doc in value!!){
                    var court: Court? = null
                    var user: User? = null
                    doc.getDocumentReference("court")?.get()
                        ?.addOnCompleteListener{ c ->
                            court = c.result.toCourt()

                            doc.getDocumentReference("user")?.get()
                                ?.addOnCompleteListener{ u ->
                                    user = u.result.toUser()
                                    reservations.add(doc.toReservation(court, user))
                                    cb(reservations)
                                }
                        }
                }
            }
    }

    override fun detachReservationsListener() {
        userReservationsListener.remove()
    }

    override suspend fun getReservationsById(id: String): Reservation? {
        val reservation = _db
            .collection("reservations")
            .document(id).get().await()

        if (reservation != null) {
            val court = reservation.getDocumentReference("court")?.get()?.await()?.toCourt()
            val user = reservation.getDocumentReference("user")?.get()?.await()?.toUser()
            return reservation.toReservation(court, user)
        }
        return null
    }

    override fun saveReservation(reservation: Reservation) {

        if (reservation.id == "") {
            _db.collection("reservations").add(
                reservation
                    .toHashMap(
                        _db.document("courts/${reservation.court?.id}"),
                        _db.document("users/${reservation.user?.id}")
                    )
            )
                .addOnSuccessListener {
                    Log.d("SAVE RESERVATION", "SUCCESS")
                }
                .addOnFailureListener {
                    Log.d("SAVE RESERVATION", "FAILURE")
                }
        } else {
            _db.collection("reservations").document(reservation.id).set(reservation.toHashMap(
                _db.document("courts/${reservation.court?.id}"),
                _db.document("users/${reservation.user?.id}")
            ))
                .addOnSuccessListener {
                    Log.d("EDIT RESERVATION", "SUCCESS")
                }
                .addOnFailureListener {
                    Log.d("EDIT RESERVATION", "FAILURE")
                }
        }
    }

    override fun deleteReservation(reservationId: String) {
        _db.collection("reservations").document(reservationId).delete()
            .addOnSuccessListener {
                Log.d("DELETE RESERVATION", "SUCCESS")
            }
            .addOnFailureListener {
                Log.d("DELETE RESERVATION", "FAILURE")
            }
    }

}