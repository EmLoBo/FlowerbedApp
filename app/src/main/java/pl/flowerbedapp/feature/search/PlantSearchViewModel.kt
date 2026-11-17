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
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.SoilType
import pl.flowerbedapp.core.domain.usecase.location.GetLocationUseCase
import pl.flowerbedapp.core.domain.usecase.plant.SearchPlantsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveFavoritePlantIdsUseCase
import javax.inject.Inject

/** pH slider bounds; the full span means "no pH filter". Garden soils don't fall outside it. */
val PH_SLIDER_RANGE = 3.5f..9.0f
const val SUN_SLIDER_MAX = 12

data class SearchUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
    val phMin: Float = PH_SLIDER_RANGE.start,
    val phMax: Float = PH_SLIDER_RANGE.endInclusive,
    val sunHours: Int? = null,           // null = any amount of sun
    val soilType: SoilType? = null,
    val moisture: Moisture? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val usingDeviceLocation: Boolean = false,
) {
    val phFilterOn: Boolean
        get() = phMin > PH_SLIDER_RANGE.start || phMax < PH_SLIDER_RANGE.endInclusive

    val hasActiveFilters: Boolean
        get() = phFilterOn || sunHours != null || soilType != null || moisture != null
}

@HiltViewModel
class PlantSearchViewModel @Inject constructor(
    private val searchPlants: SearchPlantsUseCase,
    private val getLocation: GetLocationUseCase,
    observeFavoriteIds: ObserveFavoritePlantIdsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    // Single source of truth for the typed text. The text field reads this directly, so it
    // updates on every keystroke — the debounce below must only gate the *search*, never
    // what the field displays.
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var page = 1
    private var loadJob: Job? = null

    init {
        @OptIn(FlowPreview::class)
        _query
            .debounce(450L)
            .distinctUntilChanged()
            .onEach { q -> if (q.length >= 2 || q.isEmpty()) search() }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(q: String) { _query.value = q }
    fun onPhChanged(min: Float, max: Float) = _state.update { it.copy(phMin = min, phMax = max) }
    fun onSunHoursChanged(hours: Int?) = _state.update { it.copy(sunHours = hours) }

    /** Sliders report every pixel of a drag — search once, when the finger lifts. */
    fun onSliderReleased() = search()

    fun onSoilTypeChanged(type: SoilType?) {
        _state.update { it.copy(soilType = type) }
        search()
    }

    fun onMoistureChanged(moisture: Moisture?) {
        _state.update { it.copy(moisture = moisture) }
        search()
    }

    fun clearFilters() {
        _state.update {
            it.copy(
                phMin    = PH_SLIDER_RANGE.start,
                phMax    = PH_SLIDER_RANGE.endInclusive,
                sunHours = null,
                soilType = null,
                moisture = null,
            )
        }
        search()
    }
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
            ph       = if (s.phFilterOn) s.phMin.toDouble()..s.phMax.toDouble() else null,
            sunHours = s.sunHours?.let { it..it },
            soilType = s.soilType,
            moisture = s.moisture,
            query    = _query.value,
            page     = pageToLoad,
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
