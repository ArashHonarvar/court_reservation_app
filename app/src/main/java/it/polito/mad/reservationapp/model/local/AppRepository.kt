package it.polito.mad.reservationapp.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.reservationapp.model.local.dao.BookedCourtDAO
import it.polito.mad.reservationapp.model.local.dao.CourtDAO
import it.polito.mad.reservationapp.model.local.dao.ReservationDAO
import it.polito.mad.reservationapp.model.local.dao.ReviewDAO
import it.polito.mad.reservationapp.model.local.dao.SportDAO
import it.polito.mad.reservationapp.model.local.dao.TimeslotDAO
import it.polito.mad.reservationapp.model.local.dao.UserDAO
import it.polito.mad.reservationapp.model.local.dao.UserSportDAO
import it.polito.mad.reservationapp.model.local.entity.BookedCourt
import it.polito.mad.reservationapp.model.local.entity.Reservation
import it.polito.mad.reservationapp.model.local.entity.ReservationTimeslot
import it.polito.mad.reservationapp.model.local.entity.Review
import it.polito.mad.reservationapp.model.local.entity.User
import it.polito.mad.reservationapp.model.local.entity.UserSport

class AppRepository(application: Application) {
    private val reservationDao = AppDatabase.getDatabase(application).appDao()

    fun getReservations(): LiveData<List<ReservationDAO>> {
        return reservationDao.getReservations()
    }

    fun getSports(): LiveData<List<SportDAO>>{
        return reservationDao.getSports()
    }

    fun getSportByID(sportID: Int): LiveData<SportDAO>{
        return reservationDao.getSportByID(sportID)
    }

    fun getReservationById(reservationID: Int) : LiveData<ReservationDAO>{
        return reservationDao.getReservationById(reservationID)
    }

    fun getReviewByCourtID(courtID: Int): LiveData<ReviewDAO>{
        return reservationDao.getReviewByCourtID(courtID)
    }

    fun getCourtByReservationID(reservationID: Int): LiveData<CourtDAO>{
        return reservationDao.getCourtByReservationID(reservationID)
    }

    fun getCourtByID(courtID: Int):LiveData<CourtDAO>{
        return reservationDao.getCourtByID(courtID)
    }

    fun saveReview(review: Review){
        return reservationDao.saveReview(review)
    }

    fun getCourtsBySportName(sportName: String): LiveData<List<CourtDAO>>{
        return reservationDao.getCourtsBySportName(sportName)
    }

    fun getTimeslotsByReservationID(reservationID: Int):LiveData<List<TimeslotDAO>>{
        return reservationDao.getTimeslotsByReservationID(reservationID)
    }

    fun getBookedTimeslotByCourtIDAndDate(courtID: Int, date: String): LiveData<List<TimeslotDAO>>{
        return reservationDao.getBookedTimeslotByCourtIDAndDate(courtID, date)
        /*TODO("Ancora da implementare nel nuovo repo")*/
    }

    fun getUserByID(userID: Int):LiveData<UserDAO>{
        return reservationDao.getUserByID(userID)
    }

    fun getTimeslots(): LiveData<List<TimeslotDAO>>{
        return reservationDao.getTimeslots()
    }

    fun getUserSports(userID: Int): LiveData<List<UserSportDAO>>{
        return reservationDao.getUserSports(userID)
    }

    fun saveReservation(reservation: Reservation) : Int {
        return reservationDao.saveReservation(reservation).toInt()
    }

    fun saveReservationTimeslot(reservationTimeslot: ReservationTimeslot){
        return reservationDao.saveReservationTimeslot(reservationTimeslot)
    }

    fun deleteBookedCourtByDateCourtID(date: String, courtID: Int){
        return reservationDao.deleteBookedCourtByDateCourtID(date, courtID)
        /*TODO("Ancora da implementare nel nuovo repo")*/
    }

    fun saveBookedCourt(bookedCourt: BookedCourt){
        return reservationDao.saveBookedCourt(bookedCourt)
        /*TODO("Ancora da implementare nel nuovo repo")*/
    }

    fun saveSportUserRatings(userSport: UserSport){
        return reservationDao.saveSportUserRatings(userSport)
    }

    fun saveUser(user: User){
        return reservationDao.saveUser(user)
    }

    fun deleteReservation(reservationID:Int){
        return reservationDao.deleteReservation(reservationID)
    }

    fun getCourts(): LiveData<List<CourtDAO>>{
        return reservationDao.getCourts()
    }

    fun getBookedCourtByCourtID(courtID: Int): LiveData<List<BookedCourtDAO>>{
        return reservationDao.getBookedCourtByCourtID(courtID)
        /*TODO("Ancora da implementare nel nuovo repo")*/
    }

    fun getBookedCourts():LiveData<List<BookedCourtDAO>>{
        return reservationDao.getBookedCourts()
        /*TODO("Ancora da implementare nel nuovo repo")*/
    }

}