package pl.flowerbedapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_plants",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["project_id"],
        onDelete = ForeignKey.Companion.CASCADE,
    )],
    indices = [Index("project_id")],
)
data class ProjectPlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "project_id") val projectId: Long,
    @ColumnInfo(name = "plant_id")   val plantId: Int,
    // Full plant stored as JSON — avoids a separate plants table for MVP
    @ColumnInfo(name = "plant_json") val plantJson: String,
)