// ─── Plant Database ──────────────────────────────────────────────────────────
package pl.flowerbedapp.feature.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.usecase.plant.BrowsePlantsUseCase
import javax.inject.Inject

data class DatabaseUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class PlantDatabaseViewModel @Inject constructor(browse: BrowsePlantsUseCase) : ViewModel() {
    private val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state: StateFlow<DatabaseUiState> = query
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { q -> browse(q, page = 1) }
        .map { result ->
            when (result) {
                is Result.Success -> DatabaseUiState(plants = result.data, isLoading = false)
                is Result.Error   -> DatabaseUiState(error = result.message, isLoading = false)
                Result.Loading    -> DatabaseUiState(isLoading = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DatabaseUiState())

    fun onQueryChanged(q: String) { query.value = q }
}
