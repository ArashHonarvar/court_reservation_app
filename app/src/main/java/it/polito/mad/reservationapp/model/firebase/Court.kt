package it.polito.mad.reservationapp.model.firebase

class Court(
    val id: String,
    val name: String,
    val address: String,
    val timeslots: List<String>,
    val sport_name: String
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "address" to address,
            "timeslots" to timeslots,
            "sport_name" to sport_name
        )
    }
}
