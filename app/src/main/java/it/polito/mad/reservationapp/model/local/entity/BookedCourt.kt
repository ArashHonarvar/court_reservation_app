package it.polito.mad.reservationapp.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "booked_court",
    primaryKeys = ["court_id","timeslot_id","date"],
    foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["id"],
            childColumns = ["court_id"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = Timeslot::class,
            parentColumns = ["id"],
            childColumns = ["timeslot_id"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("court_id"), Index("timeslot_id")]
)
class BookedCourt (
    @ColumnInfo(name = "court_id")
    var courtID:Int,
    @ColumnInfo(name = "timeslot_id")
    var timeslotID:Int,
    @ColumnInfo(name = "date")
    var date:String
)