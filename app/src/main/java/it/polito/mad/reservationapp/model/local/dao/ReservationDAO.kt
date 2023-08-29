package it.polito.mad.reservationapp.model.local.dao

data class ReservationDAO (
    val reservationID: Int,
    val reservation_date: String,
    val equipment: Int,
    val timeslot: String,
    val sportName: String,
    val courtName: String
)