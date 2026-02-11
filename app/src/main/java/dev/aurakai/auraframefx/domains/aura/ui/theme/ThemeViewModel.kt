package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.ThemeService
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Theme
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.ThemeCommand

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeService: ThemeService,
    private val themePrefs: ThemePrefs,
) : ViewModel() {

    private val _theme = MutableStateFlow(Theme.DARK)
    val theme: StateFlow<Theme> = _theme

    private val _color = MutableStateFlow(Color.BLUE)
    val color: StateFlow<Color> = _color

    init {
        viewModelScope.launch {
            themePrefs.themeFlow.collect {
                _theme.value = it
            }
        }
        viewModelScope.launch {
            themePrefs.colorFlow.collect {
                _color.value = it
            }
        }
    }

    fun processThemeCommand(command: String) {
        viewModelScope.launch {
            when (val themeCommand = themeService.parseThemeCommand(command)) {
                is ThemeCommand.SetTheme -> setTheme(themeCommand.theme)
                is ThemeCommand.SetColor -> setColor(themeCommand.color)
                ThemeCommand.Unknown -> { /* Do nothing */
                }
            }
        }
    }

    /**
     * Directly set the theme - instant update, persists in background
     */
    fun setTheme(newTheme: Theme) {
        _theme.value = newTheme
        viewModelScope.launch {
            themePrefs.saveTheme(newTheme)
        }
    }

    /**
     * Directly set the primary color - instant update, persists in background
     */
    fun setColor(newColor: Color) {
        _color.value = newColor
        viewModelScope.launch {
            themePrefs.saveColor(newColor)
        }
    }
}
