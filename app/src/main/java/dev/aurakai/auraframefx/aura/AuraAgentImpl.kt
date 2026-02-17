package dev.aurakai.auraframefx.aura

import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest

class AuraAgentImpl : AuraAgent() {
    /**
     * Unique identifier for this agent
     */
    override val agentName: String
        get() = TODO("Not yet implemented")

    override suspend fun processRequest(
        request: AiRequest,
        context: String,
        agentType: AgentType
    ): AgentResponse {
        TODO("Not yet implemented")
    }

    /**
     * Handle an incoming message from the inter-agent communication bus.
     */
    override suspend fun onAgentMessage(message: AgentMessage) {
        TODO("Not yet implemented")
    }
}
