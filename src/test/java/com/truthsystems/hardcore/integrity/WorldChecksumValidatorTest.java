package com.truthsystems.hardcore.integrity;

import com.truthsystems.audit.CovenantVerifier;
import com.truthsystems.hardcore.soulbind.DeathSealManager;
import com.truthsystems.hardcore.soulbind.IntegrityFlag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldChecksumValidatorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("HIK-INV-004: level.dat checksum persists and validates")
    void levelDatChecksumRoundTrip() throws Exception {
        Path worldDir = Files.createDirectories(tempDir.resolve("clean_world"));
        Files.writeString(worldDir.resolve("level.dat"), "clean");

        WorldChecksumValidator.initOrValidate(worldDir, "clean_world");

        String persisted = WorldChecksumValidator.loadPersistedChecksum(worldDir);
        assertNotNull(persisted, "FALSIFIED: level.dat hash was not persisted");
        assertTrue(CovenantVerifier.verifyWorldFileIntegrity(worldDir, "clean_world"),
                "FALSIFIED: CovenantVerifier rejected a clean level.dat");
        assertEquals(IntegrityFlag.CLEAN, DeathSealManager.getWorldIntegrity("clean_world"),
                "FALSIFIED: Clean world was not marked CLEAN");
    }

    @Test
    @DisplayName("HIK-INV-005: level.dat tampering is detected")
    void levelDatTamperDetected() throws Exception {
        Path worldDir = Files.createDirectories(tempDir.resolve("tampered_world"));
        Files.writeString(worldDir.resolve("level.dat"), "before");
        WorldChecksumValidator.initOrValidate(worldDir, "tampered_world");

        Files.writeString(worldDir.resolve("level.dat"), "after");

        assertFalse(WorldChecksumValidator.validateChecksum(worldDir, "tampered_world"),
                "FALSIFIED: Modified level.dat still verified");
        assertEquals(IntegrityFlag.TAMPERED_LEVEL_DAT, DeathSealManager.getWorldIntegrity("tampered_world"),
                "FALSIFIED: Modified level.dat did not mark the world compromised");
    }
}
