package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*


@Entity(
    tableName = "court", foreignKeys = [ForeignKey(
        entity = Sport::class,
        parentColumns = ["id"],
        childColumns = ["sport_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sport_id")]
)
class Court(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var courtID: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "sport_id")
    var sportID: Int
)