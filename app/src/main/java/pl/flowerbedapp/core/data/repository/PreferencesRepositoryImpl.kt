package pl.flowerbedapp.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.flowerbedapp.core.domain.repository.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.set

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("flowerbed_prefs")

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val ctx: Context,
) : PreferencesRepository {

    private object Keys {
        val BG_URI = stringPreferencesKey("bg_uri")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val LAST_LAT = doublePreferencesKey("last_lat")
        val LAST_LON = doublePreferencesKey("last_lon")
    }


    override fun observeBackgroundUri(): Flow<String?> =
        ctx.dataStore.data.map { it[Keys.BG_URI] }

    override fun observeIsLoggedIn(): Flow<Boolean> =
        ctx.dataStore.data.map { it[Keys.LOGGED_IN] ?: false }

    override fun observeLastLocation(): Flow<Pair<Double, Double>?> =
        ctx.dataStore.data.map { prefs ->
            val lat = prefs[Keys.LAST_LAT]
            val lon = prefs[Keys.LAST_LON]
            if (lat != null && lon != null) lat to lon else null
        }

    override suspend fun setBackgroundUri(uri: String?) {
        ctx.dataStore.edit { if (uri == null) it.remove(Keys.BG_URI) else it[Keys.BG_URI] = uri }
    }

    override suspend fun setLoggedIn(value: Boolean) {
        ctx.dataStore.edit { it[Keys.LOGGED_IN] = value }
    }

    override suspend fun setLastLocation(lat: Double, lon: Double) {
        ctx.dataStore.edit { it[Keys.LAST_LAT] = lat; it[Keys.LAST_LON] = lon }
    }

}