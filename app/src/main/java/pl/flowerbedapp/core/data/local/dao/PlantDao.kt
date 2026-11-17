package pl.flowerbedapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pl.flowerbedapp.core.data.local.entity.PlantEntity

@Dao
interface PlantDao {

    @Upsert
    suspend fun upsertAll(plants: List<PlantEntity>)

    @Query("SELECT * FROM plants WHERE scientific_name = :scientificName")
    suspend fun getByName(scientificName: String): PlantEntity?

    @Query("SELECT COUNT(*) FROM plants WHERE source = :source")
    suspend fun countBySource(source: String): Int

    /**
     * Garden filter. Ranges match by overlap: the plant's tolerated range must touch the bed's range,
     * so a single measured value works too (pass it as both min and max).
     * Nullable criteria are skipped when null; [moistures] must always list the accepted classes —
     * pass all of them for "any moisture".
     * [query] must already be normalized the same way as `search_key`.
     */
    @Query(
        """
        SELECT * FROM plants
        WHERE (:phMin IS NULL OR ph_max >= :phMin)
          AND (:phMax IS NULL OR ph_min <= :phMax)
          AND (:sunMin IS NULL OR sun_hours_max >= :sunMin)
          AND (:sunMax IS NULL OR sun_hours_min <= :sunMax)
          AND (:soil IS NULL OR soil_types LIKE '%,' || :soil || ',%')
          AND moisture IN (:moistures)
          AND (:zone IS NULL OR :zone BETWEEN hardiness_zone_min AND hardiness_zone_max)
          AND (:query = '' OR search_key LIKE '%' || :query || '%')
        ORDER BY search_key
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun find(
        phMin: Double?,
        phMax: Double?,
        sunMin: Int?,
        sunMax: Int?,
        soil: String?,
        moistures: List<String>,
        zone: Int?,
        query: String,
        limit: Int,
        offset: Int,
    ): List<PlantEntity>
}
