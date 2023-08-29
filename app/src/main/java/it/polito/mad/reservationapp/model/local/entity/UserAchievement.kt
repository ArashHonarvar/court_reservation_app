package it.polito.mad.reservationapp.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_achievement",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("user_id")]
)
class UserAchievement(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var achievementID: Int,

    @ColumnInfo(name = "user_id")
    var userID: Int,
    @ColumnInfo(name = "description")
    var description: String
)