package pl.flowerbedapp.core.domain.repository

import pl.flowerbedapp.core.domain.model.Result

interface LocationRepository {

    suspend fun getCurrentLocation(): Result<Pair<Double, Double>>
}