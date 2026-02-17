package dev.aurakai.auraframefx.domains.kai

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.domains.aura.TaskExecutionManager
import dev.aurakai.auraframefx.core.AuraFxLogger
import dev.aurakai.auraframefx.genesis.GenesisAgent
import dev.aurakai.auraframefx.core.SecurityContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskExecutionModule {

    @Provides
    @Singleton
    fun provideTaskExecutionManager(
        auraAgent: AuraAgent,
        kaiAgent: KaiAgent,
        genesisAgent: GenesisAgent,
        securityContext: SecurityContext,
        logger: AuraFxLogger
    ): TaskExecutionManager {
        return TaskExecutionManager(
            auraAgent = auraAgent,
            kaiAgent = kaiAgent,
            genesisAgent = genesisAgent,
            securityContext = securityContext,
            logger = logger
        )
    }
}


