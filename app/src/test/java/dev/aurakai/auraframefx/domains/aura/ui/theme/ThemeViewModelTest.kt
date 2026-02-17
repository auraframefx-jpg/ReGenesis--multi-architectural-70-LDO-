package dev.aurakai.auraframefx.domains.aura.ui.theme

import com.google.common.truth.Truth.assertThat
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Theme
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.ThemeService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ThemeViewModelTest {

    private lateinit var viewModel: ThemeViewModel
    private lateinit var themeService: ThemeService
    private lateinit var themePrefs: ThemePrefs

    @Before
    fun setup() {
        themeService = ThemeService()
        themePrefs = mockk(relaxed = true)

        // Mock default flows to provide initial values
        every { themePrefs.themeFlow } returns flowOf(Theme.DARK)
        every { themePrefs.colorFlow } returns flowOf(Color.BLUE)

        viewModel = ThemeViewModel(themeService, themePrefs)
    }

    @Test
    fun `test set theme command`() = runTest {
        viewModel.processThemeCommand("set theme to dark")
        assertThat(viewModel.theme.value).isEqualTo(Theme.DARK)
    }

    @Test
    fun `test set color command`() = runTest {
        viewModel.processThemeCommand("set color to red")
        assertThat(viewModel.color.value).isEqualTo(Color.RED)
    }
}
