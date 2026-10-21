package pl.flowerbedapp.core.data.local.seed

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pl.flowerbedapp.core.data.local.dao.PlantDao
import pl.flowerbedapp.core.data.mapper.toEntityOrNull
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.util.safeCall
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// Separate file from "flowerbed_prefs": Android allows only one DataStore instance per file
private val Context.seedStore: DataStore<Preferences> by preferencesDataStore("plant_seed")
private val SEEDED_VERSION = intPreferencesKey("seeded_version")
private const val SEED_FILE = "plants_seed.json"

/** Loads assets/plants_seed.json into the `plants` table — once per seed file version. */
@Singleton
class PlantSeeder @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val plantDao: PlantDao,
    moshi: Moshi,
) {
    private val adapter = moshi.adapter(SeedFileDto::class.java)

    // App start and (later) the repository may both ask at once — only one of them seeds
    private val mutex = Mutex()

    /** Returns how many plants were written; 0 when the table was already up to date. */
    suspend fun seedIfNeeded(): Result<Int> = mutex.withLock {
        withContext(Dispatchers.IO) {
            safeCall {
                val file = ctx.assets.open(SEED_FILE).bufferedReader().use { it.readText() }
                    .let { requireNotNull(adapter.fromJson(it)) { "$SEED_FILE is empty" } }

                val seededVersion = ctx.seedStore.data.first()[SEEDED_VERSION] ?: 0
                // The row count matters too: a destructive migration empties the table
                // while this DataStore survives and still claims the old version
                val upToDate = seededVersion >= file.version && plantDao.countBySource("SEED") > 0
                if (upToDate) return@safeCall 0

                val entities = file.plants.mapNotNull { dto ->
                    dto.toEntityOrNull().also {
                        if (it == null) Timber.w("Seed: skipped invalid record %s", dto.scientificName)
                    }
                }
                plantDao.upsertAll(entities)
                ctx.seedStore.edit { it[SEEDED_VERSION] = file.version }
                Timber.d("Seed: wrote %d plants (file v%d)", entities.size, file.version)
                entities.size
            }
        }
    }
}
