package dev.aurakai.auraframefx.romtools

/**
 * Returns a user-friendly display name for the [RomOperation].
 */
fun RomOperation.getDisplayName(): String = when (this) {
    RomOperation.FlashRom -> "Flashing ROM"
    RomOperation.CreateBackup -> "Creating Backup"
    RomOperation.RestoreBackup -> "Restoring Backup"
    RomOperation.GenesisOptimizations -> "Applying Optimizations"
    RomOperation.InstallRecovery -> "Installing Recovery"
    RomOperation.UnlockBootloader -> "Unlocking Bootloader"
}
