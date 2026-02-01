package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.config.GateAssetConfig
import dev.aurakai.auraframefx.ui.components.CircuitPhoenixBackground
import dev.aurakai.auraframefx.ui.components.NeuralNetworkBackground
import dev.aurakai.auraframefx.ui.components.getGenesisSubGates
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🔮 GENESIS ORACLE DRIVE HUB (Level 3)
 * The main command center for the Orchestrator's domain: AI Logic, Code, and Storage.
 */
/**
 * Composes the Oracle Drive Hub screen with a dynamic command-center style UI and a grid of genesis gates.
 *
 * The screen renders one of two high-fidelity animated backgrounds, overlays a darkened "command center"
 * tint, displays a centered two-line top app bar with a style toggle, shows a brief status banner, and
 * presents a two-column grid of selectable Genesis cards that navigate to their respective routes.
 *
 * @param navController NavController used to navigate when a Genesis card is selected or to pop the back stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleDriveHubScreen(navController: NavController) {

    var useStyleB by remember {
        mutableStateOf(GateAssetConfig.StyleMode.genesisStyle == GateAssetConfig.GateStyle.STYLE_B)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // High-Fidelity Background - Swap between Lava Apocalypse and Circuit Phoenix
        if (useStyleB) {
            NeuralNetworkBackground()
        } else {
            CircuitPhoenixBackground()
        }

        // Semi-transparent Overlay for "Command Center" feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (useStyleB) 0.5f else 0.6f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "ORACLEDRIVE",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "GENESIS ORCHESTRATION SUITE • ${if (useStyleB) "NEURAL NETWORK" else "PHOENIX CIRCUIT"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF00FF85)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            useStyleB = !useStyleB
                            GateAssetConfig.toggleGenesisStyle()
                        }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                "Toggle Style",
                                tint = Color(0xFF00FF85)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF00FF85).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF00FF85), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "The Oracle Drive is synchronized. Synchronizing neural vectors and optimizing data vein protocols across the 70-LDO architecture.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val subGates = getGenesisSubGates()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(subGates) { gate ->
                        GenesisCard(
                            title = gate.title,
                            subtitle = gate.subtitle,
                            accentColor = gate.accentColor,
                            onClick = { navController.navigate(gate.route) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays a stylized, clickable card representing a genesis gate with a title, subtitle, and accent glow.
 *
 * The card renders a circular badge with an accent-tinted icon, a bold title, and a lighter subtitle; tapping the card invokes `onClick`.
 *
 * @param title The main label shown prominently on the card.
 * @param subtitle A short descriptive line shown below the title.
 * @param accentColor The primary accent used for the badge, icon tint, and outer glow.
 * @param onClick Callback invoked when the card is tapped.
 */
@Composable
fun GenesisCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                brush = Brush.radialGradient(
                    listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}