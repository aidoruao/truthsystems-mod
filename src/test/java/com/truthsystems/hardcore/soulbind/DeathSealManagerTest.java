package com.truthsystems.hardcore.soulbind;

import com.truthsystems.audit.CovenantVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeathSealManagerTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("HIK-INV-001: sealed death logs survive a round trip")
    void sealedDeathLogRoundTrip() throws Exception {
        Path worldDir = Files.createDirectories(tempDir.resolve("test_world"));
        DeathLogEntry entry = DeathLogEntry.createSealed(
                "uuid-1", "TestPlayer", "test_world", 123L, "fell", "session-1");

        DeathSealManager.sealDeath(worldDir, entry);

        DeathLogEntry loaded = DeathSealManager.loadSeal(worldDir, "uuid-1");
        assertNotNull(loaded, "FALSIFIED: Sealed death log could not be reloaded");
        assertTrue(loaded.verifyChecksum(), "FALSIFIED: Round-tripped death log checksum no longer verifies");
        assertTrue(DeathSealManager.isSealed(worldDir, "uuid-1"),
                "FALSIFIED: Locked player was not treated as sealed after reload");
        assertTrue(CovenantVerifier.verifyDeathSealIntegrity(worldDir, "uuid-1", "test_world"),
                "FALSIFIED: CovenantVerifier rejected a valid death seal");
    }

    @Test
    @DisplayName("HIK-INV-002: death log tampering is detected")
    void tamperedDeathLogMarksWorldCompromised() throws Exception {
        Path worldDir = Files.createDirectories(tempDir.resolve("tamper_world"));
        DeathLogEntry entry = DeathLogEntry.createSealed(
                "uuid-2", "TestPlayer", "tamper_world", 456L, "fell", "session-2");
        DeathSealManager.sealDeath(worldDir, entry);

        Path sealFile = worldDir.resolve("soulbind").resolve("uuid-2.json");
        String tampered = Files.readString(sealFile).replace("\"locked\": true", "\"locked\": false");
        Files.writeString(sealFile, tampered);

        DeathLogEntry loaded = DeathSealManager.loadSeal(worldDir, "uuid-2");
        assertNotNull(loaded, "FALSIFIED: Tampered death log vanished instead of being inspected");
        assertFalse(loaded.verifyChecksum(),
                "FALSIFIED: Tampered death log still passed checksum verification");
        assertEquals(IntegrityFlag.TAMPERED_DEATH_LOG, DeathSealManager.getWorldIntegrity("tamper_world"),
                "FALSIFIED: Tampered death log did not mark the world compromised");
    }
}
