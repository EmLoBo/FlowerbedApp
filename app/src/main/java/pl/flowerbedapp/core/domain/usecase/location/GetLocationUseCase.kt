package pl.flowerbedapp.core.domain.usecase.location

import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val repo: LocationRepository,
) {
    suspend operator fun invoke(): Result<Pair<Double, Double>> =
        repo.getCurrentLocation()
}