package it.polito.mad.reservationapp.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.mad.reservationapp.model.firebase.Achievement
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.Review
import it.polito.mad.reservationapp.model.firebase.User

fun DocumentSnapshot.toReservation(court: Court?, user: User?): Reservation {
    return Reservation(
        this.id,
        this.getTimestamp("creation_timestamp")!!,
        this.getTimestamp("reserved_date")!!.toDate(),
        this.getBoolean("equipment_requested")!!,
        court,
        this.get("timeslots")!! as List<String>,
        user,
        this.get("review", Review::class.java),
    )
}

fun DocumentSnapshot.toCourt(): Court {
    return Court(
        this.id,
        this.getString("name")!!,
        this.getString("address")!!,
        this.get("timeslots") as List<String>,
        this.getString("sport_name")!!,
    )
}


fun DocumentSnapshot.toAchievement(): Achievement {
    return Achievement(
        this.getTimestamp("date")!!.toDate(),
        this.getString("title")!!,
        this.getString("description")!!
    )
}

fun HashMap<String, Any>.toAchievement(): Achievement {
    return Achievement(
        (this["date"] as Timestamp).toDate(),
        this["title"] as String,
        this["description"] as String
    )
}


fun DocumentSnapshot.toInterestedSport(achievements: List<Achievement>?): InterestedSport {
    return InterestedSport(
        this.getString("sport_name")!!,
        this.getDouble("level")!!.toFloat(),
        achievements
    )
}

fun HashMap<String, Any>.toInterestedSport(): InterestedSport {
    return if((this["level"] as? Double) == null){
        InterestedSport(
            this["sport_name"] as String,
            (this["level"] as Long).toFloat(),
            (this["achievements"] as List<HashMap<String, Any>>).map { it.toAchievement() }
        )
    }
    else{
        InterestedSport(
            this["sport_name"] as String,
            (this["level"] as Double).toFloat(),
            (this["achievements"] as List<HashMap<String, Any>>).map { it.toAchievement() }
        )
    }

}


fun DocumentSnapshot.toReview(): Review {
    return Review(
        this.getDouble("quality_rating")!!.toFloat(),
        this.getDouble("facility_rating")!!.toFloat()
    )
}

fun DocumentSnapshot.toUser(): User {
    return User(
        this.id,
        this.getString("full_name")!!,
        this.getString("city")!!,
        this.getString("nickname")!!,
        this.get("age", Int::class.java)!!,
        this.getString("bio")!!,
        this.getString("phone_number")!!,
        (this.get("interested_sports") as List<HashMap<String, Any>>).map { it.toInterestedSport() }
    )
}

fun DocumentSnapshot.toAnnouncement(owner: User): Announcement{
    return Announcement(
        this.id,
        this.getTimestamp("expiration_date")!!.toDate(),
        this.getString("info")!!,
        owner,
        this.getString("sport")!!
    )
}

