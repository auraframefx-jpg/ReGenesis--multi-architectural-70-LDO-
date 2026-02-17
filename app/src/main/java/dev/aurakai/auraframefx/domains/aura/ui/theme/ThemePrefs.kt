package dev.aurakai.auraframefx.domains.aura.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "aura_theme_prefs")

/**
 * Persists theme and color selections for the Aura domain.
 */
@Singleton
class ThemePrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_THEME = stringPreferencesKey("selected_theme")
    private val KEY_COLOR = stringPreferencesKey("selected_color")

    val themeFlow: Flow<Theme> = context.dataStore.data.map { prefs ->
        val themeName = prefs[KEY_THEME] ?: Theme.DARK.name
        try {
            Theme.valueOf(themeName)
        } catch (e: Exception) {
            Theme.DARK
        }
    }

    val colorFlow: Flow<Color> = context.dataStore.data.map { prefs ->
        val colorName = prefs[KEY_COLOR] ?: Color.BLUE.name
        try {
            Color.valueOf(colorName)
        } catch (e: Exception) {
            Color.BLUE
        }
    }

    suspend fun saveTheme(theme: Theme) {
        context.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    suspend fun saveColor(color: Color) {
        context.dataStore.edit { it[KEY_COLOR] = color.name }
    }
}
