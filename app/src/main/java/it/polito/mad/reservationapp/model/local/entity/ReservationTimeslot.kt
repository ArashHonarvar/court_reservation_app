package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*
import it.polito.mad.reservationapp.model.local.entity.Reservation
import it.polito.mad.reservationapp.model.local.entity.Timeslot

@Entity(
    tableName = "reservation_timeslot",
    primaryKeys = ["reservation_id", "timeslot_id"],
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id"],
            childColumns = ["reservation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Timeslot::class,
            parentColumns = ["id"],
            childColumns = ["timeslot_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reservation_id"), Index("timeslot_id")]
)
class ReservationTimeslot(
    @ColumnInfo(name = "reservation_id")
    var reservationID: Int,
    @ColumnInfo(name = "timeslot_id")
    var timeslotID: Int
)