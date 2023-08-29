package it.polito.mad.reservationapp.model.firebase

class Review(
    val quality_rating: Float = 0f,
    val facility_rating: Float = 0f,
    val description: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "quality_rating" to quality_rating,
            "facility_rating" to facility_rating,
            "description" to description
        )
    }
}
