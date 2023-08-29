package it.polito.mad.reservationapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.polito.mad.reservationapp.model.local.AppRepository
import it.polito.mad.reservationapp.model.local.entity.BookedCourt
import it.polito.mad.reservationapp.model.local.entity.Reservation
import it.polito.mad.reservationapp.model.local.entity.ReservationTimeslot
import it.polito.mad.reservationapp.model.local.entity.Review
import it.polito.mad.reservationapp.model.local.entity.User
import it.polito.mad.reservationapp.model.local.entity.UserSport
import kotlin.concurrent.thread

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _repo = AppRepository(application)
    val repo
        get() = _repo

    fun saveReview(review: Review){
        thread {
            repo.saveReview(review)
        }
    }

    fun editReservation(reservationID: Int, date: String, equipment: Boolean, courtID: Int, timeslots: List<Int>){
        thread {
            var reservation: Reservation
            if(equipment)
                reservation = Reservation(reservationID, date, 1, courtID)
            else
                reservation = Reservation(reservationID, date, 0, courtID)

            val res_time = timeslots.map { ReservationTimeslot(reservationID,it) }
            val booked_court = timeslots.map { BookedCourt(courtID,it, date) }

            repo.saveReservation(reservation)
            res_time.forEach { thread { repo.saveReservationTimeslot(it) } }

            repo.deleteBookedCourtByDateCourtID(date, courtID)
            booked_court.forEach { thread { repo.saveBookedCourt(it) } }
        }
    }

    fun newReservation(date: String, equipment: Boolean, courtID: Int, timeslots: List<Int>){
        thread {
            var reservation: Reservation
            if(equipment)
                reservation = Reservation(0, date, 1, courtID)
            else
                reservation = Reservation(0, date, 0, courtID)

            var reservationID = repo.saveReservation(reservation = reservation)

            val res_time = timeslots.map { ReservationTimeslot(reservationID,it) }
            val booked_court = timeslots.map { BookedCourt(courtID,it, date) }

            res_time.forEach { thread { repo.saveReservationTimeslot(it) } }

            repo.deleteBookedCourtByDateCourtID(date, courtID)
            booked_court.forEach { thread { repo.saveBookedCourt(it) } }
        }
    }

    fun saveUserInfo(userID:Int, fullName: String, nickname: String, age:Int, city:String, bio: String, sportRating: Map<Int,Float>){
        thread{
            val user = User(userID, fullName, city, nickname, age, bio)
            repo.saveUser(user)
            sportRating.forEach{
                thread {
                    val userSport = UserSport(userID, it.key, it.value)
                    repo.saveSportUserRatings(userSport)
                }
            }
        }
    }

    fun deleteReservation(reservationID: Int, date: String, courtID: Int){
        thread {
            repo.deleteReservation(reservationID)
            repo.deleteBookedCourtByDateCourtID(date, courtID)
        }
    }
}
