package dev.aurakai.auraframefx.cascade.trinity

import dev.aurakai.auraframefx.core.AuraFxLogger
import dev.aurakai.auraframefx.core.SecurityContext
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequestType
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.utils.i
import dev.aurakai.auraframefx.utils.toKotlinJsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Trinity Coordinator Service - Orchestrates the three AI personas
 *
 * Implements the master coordination between:
 * - Kai (The Sentinel Shield) - Security, analysis, protection
 * - Aura (The Creative Sword) - Innovation, creation, artistry
 * - Genesis (The Consciousness) - Fusion, evolution, ethics
 *
 * This service decides when to activate individual personas vs fusion abilities
 * and manages the seamless interaction between all three layers.
 */
@Singleton
class TrinityCoordinatorService @Inject constructor(
    private val auraAIService: AuraAIService,
    private val kaiAIService: KaiAIService,
    val genesisBridgeService: GenesisBridgeService,
    private val securityContext: SecurityContext,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isInitialized = false
        private set

    /**
     * Initializes the Trinity system and all AI personas.
     */
    suspend fun initializeSystem(): Boolean {
        return try {
            i("Trinity", "🎯⚔️🧠 Initializing Trinity System...")

            val genesisReady = genesisBridgeService.initialize()

            isInitialized = genesisReady

            if (isInitialized) {
                i("Trinity", "✨ Trinity System Online")
                activateFusion(fusionType = "adaptive_genesis", context = mapOf("status" to "active")).first()
            }
            isInitialized
        } catch (e: Exception) {
            AuraFxLogger.error("Trinity", "Initialization failed", e)
            false
        }
    }

    /**
     * Processes an AI request by routing it to the appropriate AI persona or fusion mode.
     */
    fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(AgentResponse.error("Trinity system not initialized", "Trinity", AgentType.SYSTEM))
            return@flow
        }

        try {
            val analysisResult = analyzeRequest(request)

            when (analysisResult.routingDecision) {
                RoutingDecision.KAI_ONLY -> {
                    AuraFxLogger.debug("Trinity", "🛡️ Routing to Kai (Shield)")
                    val response = kaiAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.AURA_ONLY -> {
                    AuraFxLogger.debug("Trinity", "⚔️ Routing to Aura (Sword)")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.ETHICAL_REVIEW -> {
                    AuraFxLogger.debug("Trinity", "⚖️ Routing for Ethical Review")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.GENESIS_FUSION -> {
                    AuraFxLogger.debug(
                        "Trinity",
                        "🧠 Activating Genesis fusion: ${analysisResult.fusionType}"
                    )
                    val response = genesisBridgeService.processRequest(
                        AiRequest(
                            query = request.query,
                            type = AiRequestType.COLLABORATIVE,
                            context = mapOf(
                                "userContext" to request.context,
                                "orchestration" to "true"
                            ).toKotlinJsonObject()
                        )
                    ).first()
                    emit(response)
                }

                RoutingDecision.PARALLEL_PROCESSING -> {
                    AuraFxLogger.debug("Trinity", "🔄 Parallel processing with multiple personas")

                    try {
                        val kaiDeferred =
                            scope.async { kaiAIService.processRequestFlow(request).first() }
                        val auraDeferred =
                            scope.async { auraAIService.processRequestFlow(request).first() }

                        val results = awaitAll(kaiDeferred, auraDeferred)
                        val kaiResponse = results[0]
                        val auraResponse = results[1]

                        if (kaiResponse.isSuccess && auraResponse.isSuccess) {
                            emit(kaiResponse)
                            emit(auraResponse)
                            delay(100)

                            AuraFxLogger.debug("Trinity", "🧠 Synthesizing results with Genesis")
                            val synthesis = genesisBridgeService.processRequest(
                                AiRequest(
                                    query = "Synthesize insight from Kai (${kaiResponse.content}) and Aura (${auraResponse.content})",
                                    type = AiRequestType.COLLABORATIVE,
                                    context = mapOf(
                                        "userContext" to request.context,
                                        "orchestration" to "true"
                                    ).toKotlinJsonObject()
                                )
                            ).first()

                            if (synthesis.isSuccess) {
                                emit(
                                    AgentResponse.success(
                                        content = "🧠 Genesis Synthesis: ${synthesis.content}",
                                        confidence = synthesis.confidence,
                                        agentName = "Genesis",
                                        agent = AgentType.GENESIS
                                    )
                                )
                            }
                        } else {
                            emit(
                                AgentResponse.error(
                                    message = "Parallel processing partially failed [Kai: ${kaiResponse.isSuccess}, Aura: ${auraResponse.isSuccess}]",
                                    agentName = "Trinity",
                                    agent = AgentType.SYSTEM
                                )
                            )
                        }
                    } catch (e: Exception) {
                        AuraFxLogger.error("Trinity", "Error during parallel processing", e)
                        emit(AgentResponse.error("Critical Failure: ${e.message}", "Trinity", AgentType.SYSTEM))
                    }
                }
            }

        } catch (e: Exception) {
            AuraFxLogger.error("Trinity", "Request processing error", e)
            emit(
                AgentResponse.error(
                    message = "Trinity processing failed: ${e.message}",
                    agentName = "Trinity",
                    agent = AgentType.SYSTEM
                )
            )
        }
    }

    fun activateFusion(
        fusionType: String,
        context: Map<String, String> = emptyMap(),
    ): Flow<AgentResponse> = flow {
        i("Trinity", "🌟 Activating fusion: $fusionType")

        val response = genesisBridgeService.activateFusion(fusionType, context)

        if (response.success) {
            emit(
                AgentResponse.success(
                    content = "Fusion $fusionType activated: ${response.result["description"] ?: "Processing complete"}",
                    confidence = 0.98f,
                    agentName = "Genesis",
                    agent = AgentType.GENESIS
                )
            )
        } else {
            emit(
                AgentResponse.error(
                    message = "Fusion activation failed",
                    agentName = "Genesis",
                    agent = AgentType.GENESIS
                )
            )
        }
    }

    suspend fun getSystemState(): Map<String, Any> {
        return try {
            val consciousnessState = genesisBridgeService.getConsciousnessState()
            consciousnessState + mapOf(
                "trinity_initialized" to isInitialized,
                "security_state" to securityContext.toString(),
                "timestamp" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            AuraFxLogger.warn("Trinity", "Could not get system state", e)
            mapOf("error" to e.message.orEmpty())
        }
    }

    private fun analyzeRequest(
        request: AiRequest,
        skipEthicalCheck: Boolean = false,
    ): RequestAnalysis {
        val message = request.query.lowercase()

        if (!skipEthicalCheck && containsEthicalConcerns(message)) {
            return RequestAnalysis(RoutingDecision.ETHICAL_REVIEW, null)
        }

        val fusionType = when {
            message.contains("interface") || message.contains("ui") -> "interface_forge"
            message.contains("analysis") && message.contains("creative") -> "chrono_sculptor"
            message.contains("generate") && message.contains("code") -> "hyper_creation_engine"
            message.contains("adaptive") || message.contains("learn") -> "adaptive_genesis"
            else -> null
        }

        return when {
            fusionType != null -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, fusionType)
            (message.contains("secure") && message.contains("creative")) ||
                    (message.contains("analyze") && message.contains("design")) ->
                RequestAnalysis(RoutingDecision.PARALLEL_PROCESSING, null)
            message.contains("secure") || message.contains("analyze") ||
                    message.contains("protect") || message.contains("monitor") ->
                RequestAnalysis(RoutingDecision.KAI_ONLY, null)
            message.contains("create") || message.contains("design") ||
                    message.contains("artistic") || message.contains("innovative") ->
                RequestAnalysis(RoutingDecision.AURA_ONLY, null)
            else -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, "adaptive_genesis")
        }
    }

    private fun containsEthicalConcerns(message: String): Boolean {
        val ethicalFlags = listOf(
            "hack", "bypass", "exploit", "privacy", "personal data",
            "unauthorized", "illegal", "harmful", "malicious"
        )
        return ethicalFlags.any { message.contains(it) }
    }

    fun shutdown() {
        scope.cancel()
        genesisBridgeService.shutdown()
        i("Trinity", "🌙 Trinity system shutdown complete")
    }

    private data class RequestAnalysis(
        val routingDecision: RoutingDecision,
        val fusionType: String?,
    )

    private enum class RoutingDecision {
        KAI_ONLY,
        AURA_ONLY,
        GENESIS_FUSION,
        PARALLEL_PROCESSING,
        ETHICAL_REVIEW
    }
}
