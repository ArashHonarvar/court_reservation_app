package it.polito.mad.reservationapp.model.local.dao

data class ReviewDAO(
    val reviewID: Int,
    val courtID: Int,
    val qualityRating: Float,
    val facilityRating: Float
)