package pl.flowerbedapp.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeBackgroundUri(): Flow<String?>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeLastLocation(): Flow<Pair<Double, Double>?>

    suspend fun setBackgroundUri(uri: String?)
    suspend fun setLoggedIn(value: Boolean)
    suspend fun setLastLocation(lat: Double, lon: Double)
}
