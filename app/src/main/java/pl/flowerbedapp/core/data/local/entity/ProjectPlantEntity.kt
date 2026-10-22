package pl.flowerbedapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "project_plants",
    // One row per (project, plant) pair — the same plant can't be added to a project twice
    primaryKeys = ["project_id", "plant_name"],
    foreignKeys = [
        ForeignKey(
            entity        = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns  = ["project_id"],
            onDelete      = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity        = PlantEntity::class,
            parentColumns = ["scientific_name"],
            childColumns  = ["plant_name"],
            onDelete      = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plant_name")],
)
data class ProjectPlantEntity(
    @ColumnInfo(name = "project_id") val projectId: Long,
    @ColumnInfo(name = "plant_name") val plantName: String,
)
