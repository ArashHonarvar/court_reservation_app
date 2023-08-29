package it.polito.mad.reservationapp.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var userID: Int = 0,
    @ColumnInfo(name = "full_name")
    var fullName: String,
    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "nickname")
    var nickname: String,
    @ColumnInfo(name = "age")
    var age: Int,
    @ColumnInfo(name= "bio")
    var bio: String?
)