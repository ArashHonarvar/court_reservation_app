package it.polito.mad.reservationapp.model.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.util.Date


class Reservation(
    val id: String,
    val creation_timestamp: Timestamp,
    val reserved_date: Date,
    val equipment_requested: Boolean,
    val court: Court?,
    val timeslots: List<String>,
    val user: User?,
    var review: Review?
) {
    fun toHashMap(courtRef: DocumentReference, userRef: DocumentReference): HashMap<String, Any?> {
        return hashMapOf(
            "creation_timestamp" to creation_timestamp,
            "reserved_date" to reserved_date,
            "equipment_requested" to equipment_requested,
            "court" to courtRef, // !! just to avoid errors. If the object is null an error will be thrown at runtime
            "timeslots" to timeslots,
            "user" to userRef, // !! just to avoid errors. If the object is null an error will be thrown at runtime
            "review" to review
        )
    }
}
