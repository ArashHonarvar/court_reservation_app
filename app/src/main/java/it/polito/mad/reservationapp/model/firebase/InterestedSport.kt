package it.polito.mad.reservationapp.model.firebase

class InterestedSport(
    val sport_name: String,
    val level: Float,
    val achievements: List<Achievement>?
) {
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "sport_name" to sport_name,
            "level" to level,
            "achievements" to achievements
        )
    }
}
