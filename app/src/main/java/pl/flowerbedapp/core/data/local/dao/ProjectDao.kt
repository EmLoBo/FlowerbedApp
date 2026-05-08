package pl.flowerbedapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.local.entity.ProjectPlantEntity
import pl.flowerbedapp.core.data.local.relation.ProjectWithPlants

@Dao
interface ProjectDao {

    @Transaction
    @Query("SELECT * FROM projects ORDER BY created_at DESC")
    fun observeProjectsWithPlants(): Flow<List<ProjectWithPlants>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectWithPlants(id: Long): ProjectWithPlants?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: ProjectPlantEntity)

    @Query("DELETE FROM project_plants WHERE project_id = :projectId AND plant_id = :plantId")
    suspend fun deletePlant(projectId: Long, plantId: Int)
}
