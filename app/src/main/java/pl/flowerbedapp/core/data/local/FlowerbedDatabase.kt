package pl.flowerbedapp.core.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import pl.flowerbedapp.core.data.local.dao.ProjectDao

import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.local.entity.ProjectPlantEntity


@Database(
    entities  = [ProjectEntity::class, ProjectPlantEntity::class],
    version   = 2,
    exportSchema = true,
)
abstract class FlowerbedDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
