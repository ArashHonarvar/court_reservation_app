package it.polito.mad.reservationapp.model.local.dao

data class UserSportDAO(
    val userID: Int,
    val sportID: Int,
    val sportName: String,
    val level: Float
)