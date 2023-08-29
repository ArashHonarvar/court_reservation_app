package it.polito.mad.reservationapp.model.firebase.service

import it.polito.mad.reservationapp.model.firebase.Reservation

interface ReservationService {
    /* --- RESERVATIONS --- */
    suspend fun getAllReservations(): List<Reservation>
    suspend fun getReservationsByUserId(userId: String): List<Reservation>
    suspend fun getReservationsByUserIdRealTime(userId: String, cb: (List<Reservation>)->Unit)
    fun detachReservationsListener()
    suspend fun getReservationsById(id: String): Reservation?
    fun saveReservation(reservation: Reservation)
    fun deleteReservation(reservationId: String)
}