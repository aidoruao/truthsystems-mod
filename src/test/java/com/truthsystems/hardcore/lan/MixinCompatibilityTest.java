package com.truthsystems.hardcore.lan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinCompatibilityTest {

    @Test
    @DisplayName("HIK-INV-017: LAN button filter matches only Allow Cheats labels")
    void lanMenuFilterMatchesVanillaText() {
        assertTrue(LanMenuMixin.matchesCheatToggleLabel("Allow Cheats: OFF"));
        assertTrue(LanMenuMixin.matchesCheatToggleLabel("allow cheats: on"));
        assertFalse(LanMenuMixin.matchesCheatToggleLabel("Start LAN World"));
        assertFalse(LanMenuMixin.matchesCheatToggleLabel("Game Mode: Survival"));
        assertFalse(LanMenuMixin.matchesCheatToggleLabel("Allow something else"));
    }
}
