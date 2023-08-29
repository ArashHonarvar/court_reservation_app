package it.polito.mad.reservationapp.model.firebase.service

import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.model.firebase.CardHighlights
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.Review
import it.polito.mad.reservationapp.model.firebase.User
import java.util.Date

interface FirebaseService {

    /* --- REVIEW --- */
    fun saveReviewForReservation(reservationId: String, review: Review)
    suspend fun getReviewByCourtId(courtId: String): Result<Float>

    /* --- COURTS ---*/
    suspend fun getCourts(): Result<List<Court>?>
    suspend fun getCourtById(courtId: String): Result<Court?>
    suspend fun getCourtByReservationId(resId: String): Result<Court?>
    suspend fun getCourtsBySportName(sport_name: String): Result<List<Court>?>
    suspend fun getSports(): Result<List<String>?>
    suspend fun getAvailableTimeslotByCourtIDAndDate(courtId: String, date: String): Result<List<String>?>


    /* --- App content --- */
    suspend fun getCoverImage(): ByteArray
    suspend fun getCardImages(): List<CardHighlights>
    suspend fun getHighlightsCardName(): List<String>

}