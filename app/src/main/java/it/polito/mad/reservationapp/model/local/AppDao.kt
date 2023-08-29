package it.polito.mad.reservationapp.model.local

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
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

@Dao
interface AppDao {

    @Query("SELECT R.id AS reservationID, date AS reservation_date, timeslot, equipment, S.name AS sportName, C.name AS courtName  " +
            "FROM reservation R, reservation_timeslot RT, timeslot T, sport S, court C " +
            "WHERE R.id=RT.reservation_id AND RT.timeslot_id=T.id AND R.court_id = C.id AND C.sport_id=S.id"
    )
    fun getReservations() : LiveData<List<ReservationDAO>>

    @Query("SELECT id AS sportID, name AS sportName FROM sport")
    fun getSports(): LiveData<List<SportDAO>>

    @Query("SELECT id AS sportID, name AS sportName FROM sport WHERE id=:sportID ")
    fun getSportByID(sportID: Int): LiveData<SportDAO>

    @Query("SELECT R.id AS reservationID, date AS reservation_date, equipment, timeslot, S.name AS sportName, C.name AS courtName  " +
            "FROM reservation R, reservation_timeslot RT, timeslot T, sport S, court C " +
            "WHERE R.id = :reservation_id AND R.id=RT.reservation_id AND RT.timeslot_id=T.id AND R.court_id = C.id AND C.sport_id=S.id"
    )
    fun getReservationById(reservation_id: Int): LiveData<ReservationDAO>

    @Query("SELECT id AS reviewID, court_id AS courtID, quality_rating AS qualityRating, facilities_rating AS facilityRating " +
            "FROM review " +
            "WHERE court_id=:courtID")
    fun getReviewByCourtID(courtID: Int): LiveData<ReviewDAO>

    @Query("SELECT C.id AS courtID, C.name AS courtName, S.name AS sportName, facilities_rating AS facilityRating, quality_rating AS qualityRating " +
            "FROM court C, reservation R, sport S, review REV " +
            "WHERE R.id = :reservationID AND R.court_id=C.id AND C.sport_id = S.id AND REV.court_id=C.id")
    fun getCourtByReservationID(reservationID: Int): LiveData<CourtDAO>

    @Query("SELECT C.id AS courtID, C.name AS courtName, S.name AS sportName, facilities_rating AS facilityRating, quality_rating AS qualityRating " +
            "FROM court C, sport S, review REV " +
            "WHERE C.id = :courtID AND C.sport_id = S.id AND REV.court_id=C.id")
    fun getCourtByID(courtID: Int): LiveData<CourtDAO>

    @Query("SELECT C.id AS courtID, C.name AS courtName, S.name AS sportName, facilities_rating AS facilityRating, quality_rating AS qualityRating " +
            "FROM court C, sport S, review REV " +
            "WHERE S.name = :sportName AND C.sport_id = S.id AND REV.court_id=C.id")
    fun getCourtsBySportName(sportName: String): LiveData<List<CourtDAO>>

    @Query("SELECT T.id AS timeslotID, T.timeslot AS timeslot " +
            "FROM timeslot T, reservation R, reservation_timeslot RT " +
            "WHERE R.id=:reservationID AND R.id=RT.reservation_id AND RT.timeslot_id=T.id")
    fun getTimeslotsByReservationID(reservationID: Int): LiveData<List<TimeslotDAO>>

    @Query("SELECT T.id AS timeslotID, T.timeslot AS timeslot " +
            "FROM booked_court BK, timeslot T " +
            "WHERE BK.court_id = :courtID AND BK.date=:date AND BK.timeslot_id = T.id")
    fun getBookedTimeslotByCourtIDAndDate(courtID: Int, date: String): LiveData<List<TimeslotDAO>>

    @Query("SELECT T.id AS timeslotID, T.timeslot AS timeslot, BK.date AS date, BK.court_id AS courtID, C.sport_id AS sportID " +
            "FROM booked_court BK, timeslot T, court C " +
            "WHERE BK.court_id = :courtID AND BK.timeslot_id = T.id AND BK.court_id=C.id")
    fun getBookedCourtByCourtID(courtID: Int): LiveData<List<BookedCourtDAO>>

    @Query("SELECT T.id AS timeslotID, T.timeslot AS timeslot, BK.date AS date, BK.court_id AS courtID, C.sport_id AS sportID " +
            "FROM booked_court BK, timeslot T, court C " +
            "WHERE BK.timeslot_id = T.id AND BK.court_id=C.id")
    fun getBookedCourts():LiveData<List<BookedCourtDAO>>

    @Query("SELECT id AS timeslotID, timeslot FROM timeslot")
    fun getTimeslots(): LiveData<List<TimeslotDAO>>

    @Insert(onConflict = REPLACE)
    fun saveReview(review: Review)

    @Insert(onConflict = REPLACE)
    fun saveReservation(reservation: Reservation) : Long

    @Insert
    fun saveReservationTimeslot(reservationTimeslot: ReservationTimeslot)

    @Query("DELETE FROM booked_court WHERE date=:date AND court_id=:courtID")
    fun deleteBookedCourtByDateCourtID(date: String, courtID: Int)

    @Query("SELECT id AS userID, full_name AS fullName, nickname, age, city, bio " +
            "FROM user U " +
            "WHERE U.id = :userID")
    fun getUserByID(userID: Int):LiveData<UserDAO>

    @Query("SELECT user_id AS userID, sport_id AS sportID, S.name AS sportName, level " +
            "FROM user_sport US, sport S " +
            "WHERE US.sport_id=S.id AND US.user_id=:userID")
    fun getUserSports(userID: Int): LiveData<List<UserSportDAO>>

    @Insert
    fun saveBookedCourt(bookedCourt: BookedCourt)

    @Insert(onConflict = REPLACE)
    fun saveSportUserRatings(userSport: UserSport)

    @Insert(onConflict = REPLACE)
    fun saveUser(user: User)

    @Query("DELETE FROM reservation WHERE id=:reservationID")
    fun deleteReservation(reservationID:Int)

    @Query("SELECT C.id AS courtID, C.name AS courtName, S.name AS sportName, facilities_rating AS facilityRating, quality_rating AS qualityRating " +
            "FROM court C, sport S, review REV " +
            "WHERE C.sport_id = S.id AND REV.court_id=C.id")
    fun getCourts(): LiveData<List<CourtDAO>>
}