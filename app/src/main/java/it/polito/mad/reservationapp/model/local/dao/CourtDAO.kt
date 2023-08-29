package it.polito.mad.reservationapp.model.local.dao

data class CourtDAO(
    val courtID: Int,
    val courtName: String,
    val sportName: String,
    val qualityRating: Float,
    val facilityRating: Float
)