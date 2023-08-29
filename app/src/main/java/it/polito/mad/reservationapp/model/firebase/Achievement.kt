package it.polito.mad.reservationapp.model.firebase

import java.util.Date

class Achievement(
    val date: Date,
    val title: String,
    val description: String,
) {


    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "date" to date,
            "title" to title,
            "description" to description
        )
    }
}


