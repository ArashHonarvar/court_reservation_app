package it.polito.mad.reservationapp.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.reservationapp.model.firebase.CardHighlights
import it.polito.mad.reservationapp.model.firebase.Court
import it.polito.mad.reservationapp.model.firebase.InterestedSport
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.model.firebase.Review
import it.polito.mad.reservationapp.model.firebase.User
import it.polito.mad.reservationapp.model.firebase.service.FirebaseServiceImpl
import it.polito.mad.reservationapp.model.firebase.service.ReservationServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.Result

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _firebaseRepository = FirebaseServiceImpl()
    private val firebaseRepository
        get() = _firebaseRepository

    private val _firebaseReservationService = ReservationServiceImpl()


    /* --- RESERVATIONS --- */
    fun getAllReservations(): LiveData<List<Reservation>> {
        return liveData(Dispatchers.IO) {
            val reservationsList = _firebaseReservationService.getAllReservations()
            emit(reservationsList)
        }
    }
    fun getReservationsById(id: String): LiveData<Reservation?> {
        return liveData(Dispatchers.IO) {
            val reservation = _firebaseReservationService.getReservationsById(id)
            emit(reservation)
        }
    }
    fun editReservation(reservation: Reservation) {
        viewModelScope.launch {
            _firebaseReservationService.saveReservation(reservation)
        }
    }
    fun deleteReservation(reservationId: String) {
        viewModelScope.launch {
            _firebaseReservationService.deleteReservation(reservationId)
        }
    }
    fun newReservation(reservation: Reservation) {
        viewModelScope.launch {
            _firebaseReservationService.saveReservation(reservation)
        }
    }

    /* --- --- */
    fun getCourtByReservationId(reservationId: String): LiveData<Result<Court?>> {
        return liveData(Dispatchers.IO) {
            try {
                val court = firebaseRepository.getCourtByReservationId(reservationId)
                emit(court)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getCourts(): LiveData<Result<List<Court>?>> {
        return liveData(Dispatchers.IO) {
            try {
                val courtList = firebaseRepository.getCourts()
                emit(courtList)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getCourtById(courtId: String): LiveData<Result<Court?>> {
        return liveData(Dispatchers.IO) {
            try {
                val court = firebaseRepository.getCourtById(courtId)
                emit(court)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getCourtsBySportName(sportName: String): LiveData<Result<List<Court>?>> {
        return liveData(Dispatchers.IO) {
            try {
                val court = firebaseRepository.getCourtsBySportName(sportName)
                emit(court)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getSports(): LiveData<Result<List<String>?>> {
        return liveData(Dispatchers.IO) {
            try {
                val sports = firebaseRepository.getSports()
                emit(sports)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getAvailableTimeslotByCourtIDAndDate(courtId: String, date: String): LiveData<Result<List<String>?>> {
        return liveData(Dispatchers.IO) {
            try {
                val timeslots =
                    firebaseRepository.getAvailableTimeslotByCourtIDAndDate(courtId, date)
                emit(timeslots)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }

    /* --- REVIEW --- */
    fun getReviewByCourtId(courtId: String): LiveData<Result<Float>> {
        return liveData(Dispatchers.IO) {
            try {
                val rating = firebaseRepository.getReviewByCourtId(courtId)
                emit(rating)
            } catch (e: Exception) {
                emit(Result.failure(e.cause!!))
            }
        }
    }
    fun getReviewsBySportName(sportName: String): LiveData<List<Pair<String, Result<Float>>>?> {
        return liveData(Dispatchers.IO) {
            try {

                val courts = firebaseRepository.getCourtsBySportName(sportName)
                val ratings = courts.getOrNull()?.map {
                    it.id to firebaseRepository.getReviewByCourtId(it.id)
                }
                emit(ratings)
            } catch (e: Exception) {
                emit(listOf(Pair("", Result.failure(e.cause!!))))
            }
        }
    }
    fun saveReviewForReservation(reservationId: String, review: Review) {
        viewModelScope.launch {
            firebaseRepository.saveReviewForReservation(reservationId, review)
        }
    }

}