package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*
import it.polito.mad.reservationapp.model.local.entity.Sport
import it.polito.mad.reservationapp.model.local.entity.User

@Entity(
    tableName = "user_sport",
    primaryKeys = ["user_id", "sport_id"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
    ), ForeignKey(
            entity = Sport::class,
            parentColumns = ["id"],
            childColumns = ["sport_id"],
            onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id"), Index("sport_id")]
)
class UserSport(
    @ColumnInfo(name = "user_id")
    var userID: Int,
    @ColumnInfo(name = "sport_id")
    var sportID: Int,
    @ColumnInfo(name = "level")
    var level: Float
)