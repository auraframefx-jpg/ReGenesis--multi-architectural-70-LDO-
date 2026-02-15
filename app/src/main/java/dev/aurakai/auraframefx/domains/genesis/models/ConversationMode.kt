package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Defines the conversation mode for multi-agent interactions.
 * Controls how agents take turns communicating.
 */
enum class ConversationMode {
    /** Agents communicate in a structured turn-based order */
    TURN_ORDER,
    /** Agents communicate freely without strict ordering */
    FREE_FORM
}
