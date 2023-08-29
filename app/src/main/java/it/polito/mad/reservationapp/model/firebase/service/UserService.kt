package it.polito.mad.reservationapp.model.firebase.service

import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.User

interface UserService {
    /* --- USER ---*/
    suspend fun getUserByIdRealTime(userId: String, cb :(User)->Unit)
    fun detachUserListener()
    suspend fun getUserById(userId: String): User?
    fun updateUser(user: User)
    suspend fun createUserIfNotExists(user: User)
    suspend fun getInterestedSportsByUserId(userId: String): List<InterestedSport>
    suspend fun saveInterestedSportsByUserId(userId: String, interestedSports: List<InterestedSport>)
    suspend fun getProfileImage(userId: String): ByteArray
    suspend fun getProfileImageByName(userId: String, name: String): ByteArray
    fun saveProfilePicture(userId: String, filename: String, data: ByteArray)
    suspend fun getProfileImageReference(userId: String): String?
}