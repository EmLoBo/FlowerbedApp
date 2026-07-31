package pl.flowerbedapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.SoilType
import pl.flowerbedapp.core.domain.model.SunExposure
import pl.flowerbedapp.core.domain.usecase.location.GetLocationUseCase
import pl.flowerbedapp.core.domain.usecase.plant.SearchPlantsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveFavoritePlantIdsUseCase
import javax.inject.Inject

data class SearchUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
    val query: String = "",
    val phMin: Float = 5.5f,
    val phMax: Float = 7.0f,
    val sunExposure: SunExposure = SunExposure.PARTIAL_SUN,
    val soilType: SoilType = SoilType.LOAMY,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val usingDeviceLocation: Boolean = false,
)

@HiltViewModel
class PlantSearchViewModel @Inject constructor(
    private val searchPlants: SearchPlantsUseCase,
    private val getLocation: GetLocationUseCase,
    observeFavoriteIds: ObserveFavoritePlantIdsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val queryFlow = MutableStateFlow("")

    private var page = 1
    private var loadJob: Job? = null

    init {
        @OptIn(FlowPreview::class)
        queryFlow
            .debounce(450L)
            .distinctUntilChanged()
            .onEach { q ->
                _state.update { it.copy(query = q) }
                if (q.length >= 2 || q.isEmpty()) search()
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(q: String) { queryFlow.value = q }
    fun onPhChanged(min: Float, max: Float) = _state.update { it.copy(phMin = min, phMax = max) }
    fun onSunExposureChanged(s: SunExposure) = _state.update { it.copy(sunExposure = s) }
    fun onSoilTypeChanged(t: SoilType) = _state.update { it.copy(soilType = t) }
    fun onManualLocation(lat: Double, lon: Double) =
        _state.update { it.copy(latitude = lat, longitude = lon, usingDeviceLocation = false) }

    fun useDeviceLocation() {
        viewModelScope.launch {
            when (val r = getLocation()) {
                is Result.Success -> _state.update {
                    it.copy(latitude = r.data.first, longitude = r.data.second, usingDeviceLocation = true)
                }
                is Result.Error -> _state.update { it.copy(error = "Location unavailable") }
                Result.Loading  -> Unit
            }
        }
    }

    /** New search: always starts over from page 1 and replaces the results. */
    fun search() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, isLoadingMore = false, error = null, endReached = false)
            }
            runSearch(pageToLoad = 1, append = false)
        }
    }

    /** Called when the user scrolls near the end — appends the next page. */
    fun loadMore() {
        val s = _state.value
        if (s.isLoading || s.isLoadingMore || s.endReached || s.error != null) return
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            runSearch(pageToLoad = page + 1, append = true)
        }
    }

    private suspend fun runSearch(pageToLoad: Int, append: Boolean) {
        val s = _state.value
        val params = GardenSearchParams(
            latitude = s.latitude,
            longitude = s.longitude,
            soilPhMin = s.phMin.toDouble(),
            soilPhMax = s.phMax.toDouble(),
            sunExposure = s.sunExposure,
            soilType = s.soilType,
            query = s.query,
            page = pageToLoad,
        )
        when (val result = searchPlants(params)) {
            is Result.Success -> {
                page = pageToLoad
                _state.update {
                    it.copy(
                        plants        = if (append) it.plants + result.data else result.data,
                        isLoading     = false,
                        isLoadingMore = false,
                        endReached    = result.data.size < PAGE_SIZE,
                    )
                }
            }
            is Result.Error -> _state.update {
                it.copy(
                    isLoading     = false,
                    isLoadingMore = false,
                    // keep the already-loaded results if only the extra page failed
                    error         = if (append) it.error else result.message,
                )
            }
            Result.Loading -> Unit
        }
    }

    private companion object { const val PAGE_SIZE = 20 }
}
