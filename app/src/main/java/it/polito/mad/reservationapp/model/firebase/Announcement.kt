package it.polito.mad.reservationapp.model.firebase

import com.google.firebase.firestore.DocumentReference
import java.util.Date

class Announcement(
    val id: String,
    val expiration_date: Date,
    val info: String,
    val owner: User,
    val sport: String
    ) {

    fun toHashMap(userRef: DocumentReference): HashMap<String, Any> {
        return hashMapOf(
            "expiration_date" to expiration_date,
            "info" to info,
            "owner" to userRef,
            "sport" to sport
        )
    }
}