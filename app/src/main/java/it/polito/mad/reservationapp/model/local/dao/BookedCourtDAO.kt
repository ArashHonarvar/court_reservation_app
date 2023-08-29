package it.polito.mad.reservationapp.model.local.dao

data class BookedCourtDAO (
    val courtID: Int,
    val sportID: Int,
    val timeslotID: Int,
    val timeslot: String,
    val date: String
)