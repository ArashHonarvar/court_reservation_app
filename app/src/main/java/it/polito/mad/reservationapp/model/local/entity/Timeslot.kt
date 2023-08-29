package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*
import it.polito.mad.reservationapp.model.local.entity.Court

@Entity(
    tableName = "timeslot"
)
class Timeslot(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var timeslotID: Int = 0,
    @ColumnInfo(name = "timeslot")
    var timeslot: String
)