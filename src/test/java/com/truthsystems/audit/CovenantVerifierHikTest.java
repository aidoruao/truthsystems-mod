package com.truthsystems.audit;

import net.minecraft.world.level.GameType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CovenantVerifierHikTest {

    @Test
    @DisplayName("HIK verifier: spectator lock requires spectator mode when locked")
    void verifySpectatorLockStateChecksLockedPlayers() {
        assertTrue(CovenantVerifier.verifySpectatorLockState(false, GameType.SURVIVAL),
                "FALSIFIED: Unlocked player failed spectator verification");
        assertTrue(CovenantVerifier.verifySpectatorLockState(true, GameType.SPECTATOR),
                "FALSIFIED: Locked spectator player failed verification");
        assertFalse(CovenantVerifier.verifySpectatorLockState(true, GameType.SURVIVAL),
                "FALSIFIED: Locked survival player passed verification");
    }

    @Test
    @DisplayName("HIK verifier: LAN cheats are forbidden only for Hardcore worlds")
    void verifyLanCheatBlockedStateChecksHardcore() {
        assertTrue(CovenantVerifier.verifyLanCheatBlockedState(false, true),
                "FALSIFIED: Non-hardcore world failed LAN cheat verification");
        assertTrue(CovenantVerifier.verifyLanCheatBlockedState(true, false),
                "FALSIFIED: Hardcore world without cheats failed verification");
        assertFalse(CovenantVerifier.verifyLanCheatBlockedState(true, true),
                "FALSIFIED: Hardcore world with cheats enabled passed verification");
    }
}
