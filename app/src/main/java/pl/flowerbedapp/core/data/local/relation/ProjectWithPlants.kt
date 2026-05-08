package pl.flowerbedapp.core.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.local.entity.ProjectPlantEntity

data class ProjectWithPlants(
    @Embedded val project: ProjectEntity,
    @Relation(parentColumn = "id", entityColumn = "project_id")
    val plants: List<ProjectPlantEntity>,
)