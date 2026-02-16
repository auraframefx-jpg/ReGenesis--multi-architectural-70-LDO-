package dev.aurakai.auraframefx.romtools

import android.os.Build
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderSafetyManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.romtools.retention.RetentionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main manager for ROM Tools operations.
 * Coordinates all ROM-related functionality including flashing, backup, recovery, and system modifications.
 */
@Singleton
open class RomToolsManager @Inject constructor(
    private val bootloaderManager: BootloaderManager,
    private val safetyManager: BootloaderSafetyManager,
    private val recoveryManager: RecoveryManager,
    private val flashManager: FlashManager,
    private val backupManager: BackupManager,
    private val systemModificationManager: SystemModificationManager,
    private val verificationManager: RomVerificationManager,
    private val retentionManager: AurakaiRetentionManager
) {
    private val _romToolsState = MutableStateFlow(RomToolsState())
    val romToolsState: StateFlow<RomToolsState> = _romToolsState.asStateFlow()

    private val _operationProgress = MutableStateFlow<OperationProgress?>(null)
    val operationProgress: StateFlow<OperationProgress?> = _operationProgress.asStateFlow()

    init {
        initializeRomTools()
    }

    private fun initializeRomTools() {
        try {
            val capabilities = RomCapabilities(
                hasRootAccess = checkRootAccess(),
                hasBootloaderAccess = bootloaderManager.checkBootloaderAccess(),
                hasRecoveryAccess = recoveryManager.checkRecoveryAccess(),
                hasSystemWriteAccess = systemModificationManager.checkSystemWriteAccess(),
                supportedArchitectures = getSupportedArchitectures(),
                deviceModel = Build.MODEL,
                androidVersion = Build.VERSION.RELEASE,
                securityPatchLevel = Build.VERSION.SECURITY_PATCH
            )

            _romToolsState.value = RomToolsState(
                isInitialized = true,
                capabilities = capabilities
            )

            Timber.i("ROM Tools initialized successfully: $capabilities")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize ROM Tools")
            _romToolsState.value = RomToolsState(
                isInitialized = false
            )
        }
    }

    open suspend fun processRomOperation(operation: RomOperation): dev.aurakai.auraframefx.domains.genesis.models.AgentResponse {
        return try {
            Timber.i("Processing ROM operation: $operation")
            dev.aurakai.auraframefx.domains.genesis.models.AgentResponse.success(
                content = "ROM operation processed successfully",
                agentName = "RomTools",
                agentType = dev.aurakai.auraframefx.domains.genesis.models.AgentType.GENESIS
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to process ROM operation")
            dev.aurakai.auraframefx.domains.genesis.models.AgentResponse.error(
                message = e.message ?: "Unknown error",
                agentName = "RomTools",
                agentType = dev.aurakai.auraframefx.domains.genesis.models.AgentType.GENESIS
            )
        }
    }

    open suspend fun flashRom(romFile: RomFile): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.VERIFYING_ROM, 0f)
            verificationManager.verifyRomFile(romFile).getOrThrow()
            updateOperationProgress(RomStep.FLASHING_ROM, 20f)
            flashManager.flashRom(romFile) { progress ->
                updateOperationProgress(RomStep.FLASHING_ROM, 20f + (progress * 80f))
            }.getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open suspend fun createNandroidBackup(backupName: String): Result<BackupInfo> {
        return try {
            updateOperationProgress(RomStep.CREATING_BACKUP, 0f)
            val result = backupManager.createNandroidBackup(backupName) { progress ->
                updateOperationProgress(RomStep.CREATING_BACKUP, progress)
            }
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            result
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open suspend fun restoreNandroidBackup(backupInfo: BackupInfo): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.RESTORING_BACKUP, 0f)
            val result = backupManager.restoreNandroidBackup(backupInfo) { progress ->
                updateOperationProgress(RomStep.RESTORING_BACKUP, progress)
            }
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            result
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open suspend fun installGenesisOptimizations(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.APPLYING_OPTIMIZATIONS, 0f)
            val result = systemModificationManager.installGenesisOptimizations { progress ->
                updateOperationProgress(RomStep.APPLYING_OPTIMIZATIONS, progress)
            }
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            result
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open fun getAvailableRoms(): Result<List<AvailableRom>> {
        return try {
            Result.success(romRepository.getCompatibleRoms(Build.MODEL))
        } catch (e: Exception) {
            Timber.e(e, "Failed to get available ROMs")
            Result.failure(e)
        }
    }

    open fun downloadRom(rom: AvailableRom): Flow<DownloadProgress> {
        return flashManager.downloadRom(rom)
    }

    open suspend fun setupAurakaiRetention(): Result<RetentionStatus> {
        return try {
            updateOperationProgress(RomStep.SETTING_UP_RETENTION, 0f)
            val result = retentionManager.setupRetentionMechanisms()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            result
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open suspend fun unlockBootloader(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.UNLOCKING_BOOTLOADER, 0f)
            // Note: In the local version, safetyManager was used here
            val safetyResult = safetyManager.performPreFlightChecks(dev.aurakai.auraframefx.romtools.bootloader.BootloaderOperation.UNLOCK)
            if (!safetyResult.passed) {
                return Result.failure(IllegalStateException("Safety Check Failed"))
            }
            bootloaderManager.unlockBootloader().getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    open suspend fun installRecovery(): Result<Unit> {
        return try {
            updateOperationProgress(RomStep.INSTALLING_RECOVERY, 0f)
            recoveryManager.installCustomRecovery().getOrThrow()
            updateOperationProgress(RomStep.COMPLETED, 100f)
            clearOperationProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            updateOperationProgress(RomStep.FAILED, 0f)
            clearOperationProgress()
            Result.failure(e)
        }
    }

    private fun updateOperationProgress(step: RomStep, progress: Float) {
        _operationProgress.value = OperationProgress(step, progress)
    }

    private fun clearOperationProgress() {
        _operationProgress.value = null
    }

    private fun checkRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c 'echo test'")
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getSupportedArchitectures(): List<String> {
        return Build.SUPPORTED_ABIS.toList()
    }

    companion object {
        private val romRepository = RomRepository()
    }
}

// Data classes
data class RomToolsState(
    val capabilities: RomCapabilities? = null,
    val isInitialized: Boolean = false,
    val settings: RomToolsSettings = RomToolsSettings(),
    val availableRoms: List<AvailableRom> = emptyList(),
    val backups: List<BackupInfo> = emptyList()
)

data class RomCapabilities(
    val hasRootAccess: Boolean,
    val hasBootloaderAccess: Boolean,
    val hasRecoveryAccess: Boolean,
    val hasSystemWriteAccess: Boolean,
    val supportedArchitectures: List<String>,
    val deviceModel: String,
    val androidVersion: String,
    val securityPatchLevel: String
)

data class RomToolsSettings(
    val autoBackup: Boolean = true,
    val verifyRomSignatures: Boolean = true,
    val enableGenesisOptimizations: Boolean = true,
    val maxBackupCount: Int = 5,
    val downloadDirectory: String = ""
)

data class OperationProgress(
    val operation: RomStep,
    val progress: Float
) {
    fun getDisplayName(): String = operation.getDisplayName()
}

/**
 * Represents the different types of ROM Operation Steps (Progress).
 */
enum class RomStep {
    SETTING_UP_RETENTION,
    VERIFYING_ROM,
    CREATING_BACKUP,
    UNLOCKING_BOOTLOADER,
    INSTALLING_RECOVERY,
    FLASHING_ROM,
    VERIFYING_INSTALLATION,
    RESTORING_AURAKAI,
    RESTORING_BACKUP,
    APPLYING_OPTIMIZATIONS,
    DOWNLOADING_ROM,
    COMPLETED,
    FAILED;

    fun getDisplayName(): String = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

data class RomFile(
    val name: String,
    val path: String,
    val size: Long = 0L,
    val checksum: String = ""
)

data class DeviceInfo(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val securityPatchLevel: String,
    val bootloaderVersion: String
) {
    companion object {
        fun getCurrentDevice(): DeviceInfo {
            return DeviceInfo(
                model = Build.MODEL,
                manufacturer = Build.MANUFACTURER,
                androidVersion = Build.VERSION.RELEASE,
                securityPatchLevel = Build.VERSION.SECURITY_PATCH,
                bootloaderVersion = Build.BOOTLOADER
            )
        }
    }
}

data class BackupInfo(
    val name: String,
    val path: String,
    val size: Long,
    val createdAt: Long,
    val deviceModel: String,
    val androidVersion: String,
    val partitions: List<String>
)

data class AvailableRom(
    val name: String,
    val version: String,
    val androidVersion: String,
    val downloadUrl: String,
    val size: Long,
    val checksum: String,
    val description: String = "",
    val maintainer: String = "",
    val releaseDate: Long = 0L
)

data class DownloadProgress(
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val progress: Float,
    val speed: Long,
    val isCompleted: Boolean = false,
    val error: String? = null
)

class RomRepository {
    fun getCompatibleRoms(deviceModel: String): List<AvailableRom> = emptyList()
}
