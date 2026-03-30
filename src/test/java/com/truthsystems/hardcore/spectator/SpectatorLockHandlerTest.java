package com.truthsystems.hardcore.spectator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpectatorLockHandlerTest {

    @Test
    @DisplayName("HIK-INV-010: locked players stay locked until explicitly released")
    void lockAndUnlockTransitionsAreExplicit() {
        SpectatorLockHandler.lockPlayer("uuid-lock");
        assertTrue(SpectatorLockHandler.isLocked("uuid-lock"),
                "FALSIFIED: Locked player was not tracked as locked");

        SpectatorLockHandler.unlockPlayer("uuid-lock");
        assertFalse(SpectatorLockHandler.isLocked("uuid-lock"),
                "FALSIFIED: Explicit unlock did not clear spectator lock");
    }

    @Test
    @DisplayName("HIK-INV-011: blocked command matching is narrow and intentional")
    void blockedCommandMatchingUsesCommandWord() {
        assertTrue(CommandInterceptor.isBlockedCommand("/gamemode spectator"),
                "FALSIFIED: /gamemode was not blocked");
        assertTrue(CommandInterceptor.isBlockedCommand("/give @p dirt"),
                "FALSIFIED: /give was not blocked");
        assertFalse(CommandInterceptor.isBlockedCommand("givemethis"),
                "FALSIFIED: Non-command prefix was blocked as if it were give");
    }
}
