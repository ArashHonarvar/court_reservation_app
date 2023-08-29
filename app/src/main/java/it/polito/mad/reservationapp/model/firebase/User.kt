package it.polito.mad.reservationapp.model.firebase

class User(
    val id: String?,
    val full_name: String?,
    val city: String?,
    val nickname: String?,
    val age: Int?,
    val bio: String?,
    val phone_number: String?,
    var interested_sports: List<InterestedSport>?
) {
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "full_name" to full_name,
            "city" to city,
            "nickname" to nickname,
            "age" to age,
            "bio" to bio,
            "phone_number" to phone_number,
            "interested_sports" to interested_sports
        )
    }
}


