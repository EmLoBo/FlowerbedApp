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

    @Query("SELECT * FROM projects WHERE is_favorites = 1 LIMIT 1")
    suspend fun getFavoritesProject(): ProjectEntity?

    /** Names of plants in the favorites project — drives the ♥ badge on plant cards. */
    @Query(
        """
        SELECT plant_name FROM project_plants
        WHERE project_id = (SELECT id FROM projects WHERE is_favorites = 1 LIMIT 1)
        """
    )
    fun observeFavoritePlantNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    // `is_favorites = 0` guard makes the favorites project impossible to delete
    @Query("DELETE FROM projects WHERE id = :id AND is_favorites = 0")
    suspend fun deleteProject(id: Long)

    // Adding a plant that is already in the project is a no-op
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlant(plant: ProjectPlantEntity)

    @Query("DELETE FROM project_plants WHERE project_id = :projectId AND plant_name = :plantName")
    suspend fun deletePlant(projectId: Long, plantName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM project_plants WHERE project_id = :projectId AND plant_name = :plantName)")
    suspend fun containsPlant(projectId: Long, plantName: String): Boolean
}
