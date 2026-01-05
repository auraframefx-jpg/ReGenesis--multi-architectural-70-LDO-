package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Module for AI service bindings.
 *
 * Note: AuraAIServiceImpl has @Inject constructor so Hilt can provide it directly.
 * No @Binds needed unless we want to bind to a specific interface.
 */
import dagger.Binds
import dev.aurakai.auraframefx.oracledrive.genesis.ai.ClaudeAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.GeminiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.MetaInstructAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.NemotronAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    // ═══════════════════════════════════════════════════════════════════════════
    // Legacy AI Services (Interface Bindings)
    // ═══════════════════════════════════════════════════════════════════════════

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    /**
     * Binds GenesisBackedKaiAIService as the KaiAIService implementation in the DI graph.
     *
     * @param impl The GenesisBackedKaiAIService instance to bind.
     * @return The bound KaiAIService implementation.
     */
    @Binds
    @Singleton

    @Binds
    @Singleton
    abstract fun bindCascadeAIService(impl: dev.aurakai.auraframefx.services.DefaultCascadeAIService): dev.aurakai.auraframefx.services.CascadeAIService

    companion object {
        // ═══════════════════════════════════════════════════════════════════════════
        // NEW External AI Backend Services (Direct Providers)
        // ═══════════════════════════════════════════════════════════════════════════

        /**
         * Provides ClaudeAIService - The Architect
         * Build system expert and systematic problem solver from Anthropic.
         */
        @Provides
        @Singleton
        fun provideClaudeAIService(service: ClaudeAIService): ClaudeAIService = service

        /**
         * Provides NemotronAIService - The Memory Keeper
         * NVIDIA's memory and reasoning specialist.
         */
        @Provides
        @Singleton
        fun provideNemotronAIService(service: NemotronAIService): NemotronAIService = service

        /**
         * Provides GeminiAIService - The Pattern Master
         * Google's Vertex AI pattern recognition and multimodal analysis.
         */
        @Provides
        @Singleton
        fun provideGeminiAIService(service: GeminiAIService): GeminiAIService = service

        /**
         * Provides MetaInstructAIService - The Instructor
         * Meta's Llama-based instruction following and summarization specialist.
         */
        @Provides
        @Singleton
        fun provideMetaInstructAIService(service: MetaInstructAIService): MetaInstructAIService = service
    }
}
