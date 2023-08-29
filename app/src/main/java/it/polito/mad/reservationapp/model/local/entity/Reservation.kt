package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*

@Entity(
    tableName = "reservation",
    foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["id"],
            childColumns = ["court_id"],
            onDelete = ForeignKey.CASCADE
    )]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var reservationID: Int = 0,

    @ColumnInfo(name = "date")
    var reservation_date: String,

    @ColumnInfo(name = "equipment")
    var equipment: Int,

    @ColumnInfo(name = "court_id")
    var courtID: Int,
)