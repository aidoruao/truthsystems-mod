package com.truthsystems.hardcore.backup;

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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackupDetectorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("HIK-INV-007: rollback is detected when game time moves backwards")
    void rollbackDetectionTriggersOnLowerTick() throws Exception {
        Path worldDir = Files.createDirectories(tempDir.resolve("rollback_world"));
        BackupDetector.recordTick(worldDir, "rollback_world", 600L);

        assertTrue(BackupDetector.detectRollback(worldDir, "rollback_world", 100L),
                "FALSIFIED: Lower game tick was not recognized as rollback");
        BackupDetector.onWorldTick(worldDir, "rollback_world", 100L);
        assertFalse(CovenantVerifier.verifySessionContinuity(worldDir, "rollback_world", 100L),
                "FALSIFIED: CovenantVerifier accepted a rolled-back session");
        assertEquals(IntegrityFlag.ROLLBACK_DETECTED, DeathSealManager.getWorldIntegrity("rollback_world"),
                "FALSIFIED: Rollback did not mark the world compromised");
    }

    @Test
    @DisplayName("HIK-INV-009: session IDs stay stable per world and differ across worlds")
    void sessionIdsAreStableAndUnique() throws Exception {
        Path worldA = Files.createDirectories(tempDir.resolve("world_a"));
        Path worldB = Files.createDirectories(tempDir.resolve("world_b"));

        String worldAFirst = SessionIdManager.getOrCreateSessionId(worldA, "world_a");
        String worldASecond = SessionIdManager.getOrCreateSessionId(worldA, "world_a");
        String worldBId = SessionIdManager.getOrCreateSessionId(worldB, "world_b");

        assertEquals(worldAFirst, worldASecond,
                "FALSIFIED: Session ID changed within the same world");
        assertNotEquals(worldAFirst, worldBId,
                "FALSIFIED: Different worlds shared the same session ID");
    }
}
