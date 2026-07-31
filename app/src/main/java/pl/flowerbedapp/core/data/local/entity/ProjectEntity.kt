package pl.flowerbedapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    // Marks the single auto-created "favorites" project: pinned and not deletable
    @ColumnInfo(name = "is_favorites") val isFavorites: Boolean = false,
)