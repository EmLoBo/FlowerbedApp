package pl.flowerbedapp.core.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import pl.flowerbedapp.core.data.local.entity.PlantEntity
import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.local.entity.ProjectPlantEntity

data class ProjectWithPlants(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "scientific_name",
        associateBy  = Junction(
            value        = ProjectPlantEntity::class,
            parentColumn = "project_id",
            entityColumn = "plant_name",
        ),
    )
    val plants: List<PlantEntity>,
)
