package it.polito.mad.reservationapp.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sport"
)
class Sport(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var sportID: Int = 0,
    @ColumnInfo(name = "name")
    var name: String
)