package it.polito.mad.reservationapp.model.local.dao


data class UserDAO(
    val fullName: String,
    val nickname: String,
    val age: Int,
    val city: String,
    val bio : String?
)