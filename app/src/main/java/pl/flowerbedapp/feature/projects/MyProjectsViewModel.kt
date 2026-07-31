package pl.flowerbedapp.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.core.domain.usecase.project.DeleteProjectUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveProjectsUseCase
import pl.flowerbedapp.core.domain.usecase.project.SaveProjectUseCase
import javax.inject.Inject

@HiltViewModel
class MyProjectsViewModel @Inject constructor(
    observe: ObserveProjectsUseCase,
    private val save: SaveProjectUseCase,
    private val delete: DeleteProjectUseCase,
) : ViewModel() {

    // Favorites is pinned to the top; the rest keep their newest-first order
    val projects: StateFlow<List<Project>> = observe()
        .map { list -> list.sortedByDescending { it.isFavorites } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createProject(name: String, description: String) {
        viewModelScope.launch {
            save(Project(name = name, description = description))
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch { delete(id) }
    }
}
