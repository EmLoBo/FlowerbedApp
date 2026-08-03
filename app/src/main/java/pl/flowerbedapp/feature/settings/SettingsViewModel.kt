package pl.flowerbedapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesRepository,
) : ViewModel() {

    /** null = not chosen yet, callers should fall back to the system setting. */
    val darkTheme: StateFlow<Boolean?> = prefs.observeDarkTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(value) }
    }
}
