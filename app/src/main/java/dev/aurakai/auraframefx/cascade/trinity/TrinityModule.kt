package dev.aurakai.auraframefx.cascade.trinity

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.AuraFxLogger
import dev.aurakai.auraframefx.core.SecurityContext
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import javax.inject.Singleton

/**
 * Dependency Injection module for the Trinity AI system.
 *
 * Provides instances of:
 * - Genesis Bridge Service (Python backend connection)
 * - Trinity Coordinator Service (orchestrates all personas)
 * - Integration with existing Kai and Aura services
 */
@Module
@InstallIn(SingletonComponent::class)
object TrinityModule {

    @Provides
    @Singleton
    fun provideGenesisBridgeService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        @ApplicationContext applicationContext: Context,
        logger: AuraFxLogger,
    ): GenesisBridgeService {
        return GenesisBridgeService(
            auraAIService = auraAIService,
            kaiAIService = kaiAIService,
            vertexAIClient = vertexAIClient,
            contextManager = contextManager,
            securityContext = securityContext,
            applicationContext = applicationContext,
            logger = logger
        )
    }

    @Provides
    @Singleton
    fun provideTrinityCoordinatorService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        genesisBridgeService: GenesisBridgeService,
        securityContext: SecurityContext
    ): TrinityCoordinatorService {
        return TrinityCoordinatorService(
            auraAIService = auraAIService,
            kaiAIService = kaiAIService,
            genesisBridgeService = genesisBridgeService,
            securityContext = securityContext
        )
    }

}
