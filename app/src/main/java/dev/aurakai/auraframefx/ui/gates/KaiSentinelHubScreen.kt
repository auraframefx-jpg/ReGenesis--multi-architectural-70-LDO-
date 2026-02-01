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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
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
import dev.aurakai.auraframefx.ui.components.IcyTundraBackground
import dev.aurakai.auraframefx.ui.components.ShieldGridBackground
import dev.aurakai.auraframefx.ui.components.getKaiSubGates
import dev.aurakai.auraframefx.ui.components.hologram.CardStyle
import dev.aurakai.auraframefx.ui.components.hologram.HolographicCard
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🛡️ KAI SENTINEL HUB (Level 3)
 * The main control panel for the Sentinel Fortress ecosystem.
 * Consolidates ROM management, Bootloader, and Root protocols.
 */
/**
 * Displays the Kai Sentinel Hub screen with a toggleable visual style and an interactive grid of sentinel tools.
 *
 * The top app bar lets the user switch between two visual modes (Style A and Style B); switching updates local UI state and persists the choice via GateAssetConfig.toggleKaiStyle(). The screen shows a styled header and a LazyVerticalGrid of tools retrieved from getKaiSubGates(). Each tool card displays an accent-tinted holographic rune (with drawable selection and fallback) and navigates to the tool's route when tapped.
 *
 * @param navController Navigation controller used to handle back navigation and to navigate to tool routes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaiSentinelHubScreen(navController: NavController) {

    var useStyleB by remember {
        mutableStateOf(GateAssetConfig.StyleMode.kaiStyle == GateAssetConfig.GateStyle.STYLE_B)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // High-Fidelity Background - Swap between Icy Tundra and Shield Grid
        if (useStyleB) {
            ShieldGridBackground()
        } else {
            IcyTundraBackground()
        }

        // Semi-transparent Overlay for "Fortress" feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (useStyleB) 0.6f else 0.5f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "SENTINEL'S FORTRESS",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "KAI'S SECURITY DOMAIN • ${if (useStyleB) "GRID PROTOCOL" else "ICY FORTRESS"}",
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
                            GateAssetConfig.toggleKaiStyle()
                        }) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Toggle Style",
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = Color(0xFFFF3366), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Fortress Access Granted. Monitoring system integrity and root permissions. All operations are logged to the System Journal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Tools Grid
                val tools = getKaiSubGates()

                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(tools) { tool ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(tool.route)
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Determine which drawable to show based on style
                            val drawableName = if (useStyleB) tool.styleBDrawable else tool.styleADrawable
                            val drawableId = androidx.compose.ui.platform.LocalContext.current.resources.getIdentifier(
                                drawableName, "drawable", androidx.compose.ui.platform.LocalContext.current.packageName
                            ).let {
                                if (it == 0 && tool.fallbackDrawable != null) {
                                    androidx.compose.ui.platform.LocalContext.current.resources.getIdentifier(
                                        tool.fallbackDrawable,
                                        "drawable",
                                        androidx.compose.ui.platform.LocalContext.current.packageName
                                    )
                                } else it
                            }

                            HolographicCard(
                                runeRes = if (drawableId != 0) drawableId else dev.aurakai.auraframefx.R.drawable.gate_sentinel_fortress,
                                glowColor = tool.accentColor,
                                style = CardStyle.PROTECTIVE,
                                elevation = 12.dp,
                                spotColor = tool.accentColor,
                                width = 160.dp,
                                height = 220.dp,
                                iconSize = 80.dp,
                                modifier = Modifier
                            )

                            // Overlay text on card
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = tool.title,
                                    fontFamily = LEDFontFamily,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = tool.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SentinelCard(tool: SentinelToolCard, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(tool.accentColor.copy(alpha = 0.6f), Color.Transparent)
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212).copy(alpha = 0.8f)
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(tool.accentColor.copy(alpha = 0.15f))
                    .border(1.dp, tool.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    tool.icon,
                    null,
                    tint = tool.accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = tool.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}