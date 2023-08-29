package it.polito.mad.reservationapp.model.local.entity

import androidx.room.*
import it.polito.mad.reservationapp.model.local.entity.Court


@Entity(
    tableName = "review",
    foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["id"],
            childColumns = ["court_id"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("court_id")]
)
class Review(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var reviewID: Int = 0,
    @ColumnInfo(name = "court_id")
    var courtID: Int,
    @ColumnInfo(name = "quality_rating")
    var qualityRating: Float,
    @ColumnInfo(name = "facilities_rating")
    var facilityRating: Float
)