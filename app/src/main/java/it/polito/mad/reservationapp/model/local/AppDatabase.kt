package it.polito.mad.reservationapp.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.polito.mad.reservationapp.model.local.entity.BookedCourt
import it.polito.mad.reservationapp.model.local.entity.Court
import it.polito.mad.reservationapp.model.local.entity.Reservation
import it.polito.mad.reservationapp.model.local.entity.ReservationTimeslot
import it.polito.mad.reservationapp.model.local.entity.Review
import it.polito.mad.reservationapp.model.local.entity.Sport
import it.polito.mad.reservationapp.model.local.entity.Timeslot
import it.polito.mad.reservationapp.model.local.entity.User
import it.polito.mad.reservationapp.model.local.entity.UserAchievement
import it.polito.mad.reservationapp.model.local.entity.UserSport

@Database(
    entities = [
        Reservation::class, Court::class, ReservationTimeslot::class,
        Timeslot::class, User::class, Sport::class, UserSport::class,
        Review::class, BookedCourt::class, UserAchievement::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "reservation_database"
                )
                    .createFromAsset("database/reservations.db")
                    .build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}
