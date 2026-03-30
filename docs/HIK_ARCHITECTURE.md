# HIK Architecture — Hardcore Integrity Keeper

## What is HIK?

The **Hardcore Integrity Keeper (HIK)** is a subsystem of the TruthSystems Forge mod that
enforces the *permanence of death* in Minecraft Hardcore worlds.  It combines cryptographic
sealing, tamper detection, spectator-lock enforcement, and cheat-flag watching to make
Hardcore truly hardcore — even against external save-file manipulation or LAN cheat toggles.

HIK operates under the Σ_LORA_COVENANT v1.0 and is governed by the **LOGOS** and
**CHALCEDON** principles: every integrity state is explicitly named, every check is
cryptographically falsifiable, and the infrastructure exists to serve the player's chosen
challenge — not to override it.

---

## Config Keys (`truthsystems-hik.toml`)

| Key | Default | Description |
|---|---|---|
| `enable_hik` | `true` | Master switch for the entire HIK subsystem |
| `archive_path` | `"saves/archived"` | Directory for archived Hardcore world copies |
| `spectator_grace_minutes` | `10` | Minutes before an archived world is flagged for archival |
| `verification_api_port` | `0` | HTTP verification API port (0 = disabled, Phase 2) |
| `allow_sealed_backups` | `true` | Allow server-side backups even after a death seal |
| `strict_inventory_provenance` | `false` | Full inventory provenance tracking (Phase 2, no-op) |
| `compromise_on_lan_cheats` | `true` | Mark world COMPROMISED when LAN cheats are enabled |
| `read_only_on_external_cheat_flag` | `true` | Block block-break/place when world is not CLEAN |

---

## Architecture Overview

HIK is organized into five subsystems under `com.truthsystems.hardcore`:

### 1. `soulbind` — Death Sealing

| Class | Role |
|---|---|
| `IntegrityFlag` | Enum of all possible world integrity states |
| `DeathLogEntry` | POJO holding death record; self-checksums via SHA-256 |
| `DeathSealManager` | Persists/loads death seals in `<worldDir>/soulbind/<uuid>.json` |
| `SoulbindEventHandler` | Forge event handler: creates seals on death, enforces spectator on login |

**Flow**: Player dies in Hardcore → `LivingDeathEvent` → `DeathLogEntry` created with
`computeChecksum()` → written to disk → player set to SPECTATOR.  On next login,
`PlayerLoggedInEvent` checks `isSealed()` and re-enforces spectator if necessary.

### 2. `integrity` — World File Tamper Detection

| Class | Role |
|---|---|
| `WorldChecksumValidator` | Computes SHA-256 of `level.dat`; persists to `hik_integrity.json` |
| `LevelDatWatcher` | Listens to `ServerStartingEvent`; delegates to `WorldChecksumValidator` |
| `IntegrityOverlay` | Client-side HUD overlay (green/red label showing integrity state) |

**Flow**: Server starts → `LevelDatWatcher.onServerStarting` → `initOrValidate` reads
`hik_integrity.json`; if hash differs, `markCompromised(TAMPERED_LEVEL_DAT)`.

### 3. `backup` — Session Continuity & Rollback Detection

| Class | Role |
|---|---|
| `SessionIdManager` | Generates/persists stable UUID per world in `hik_session.json` |
| `BackupDetector` | Persists game tick every 600 ticks; rollback if tick goes backwards |

**Flow**: Every 600 ticks (`≈30 s`), `BackupDetector.onLevelTick` persists the current
game time.  If the recorded tick is *higher* than the current tick, a rollback is
inferred and the world is marked `ROLLBACK_DETECTED`.

### 4. `spectator` — Lock Enforcement & Archive Countdown

| Class | Role |
|---|---|
| `SpectatorLockHandler` | In-memory set of locked UUIDs; per-tick spectator re-enforcement |
| `ArchiveCountdown` | Countdown map (uuid → expiry ms); triggers archive-pending log |
| `CommandInterceptor` | Blocks `gamemode`, `gm`, `give`, `tp`, `teleport`, `kill` for locked players |

**Flow**: On death seal → `SpectatorLockHandler.lockPlayer(uuid)` +
`ArchiveCountdown.startCountdown(uuid)`.  Per tick, spectator mode is re-applied.
Commands are intercepted via `CommandEvent`.

### 5. `lan` — LAN Cheat & Read-Only Enforcement

| Class | Role |
|---|---|
| `LanMenuMixin` | Mixin on `ShareToLanScreen`; disables "Allow Cheats" button on Hardcore worlds |
| `CheatFlagWatcher` | Server tick watcher; detects `getAllowCommands()` transition to `true` |
| `ReadOnlyWorldEnforcer` | Cancels `BlockEvent.BreakEvent` / `EntityPlaceEvent` when integrity ≠ CLEAN |

---

## Integrity State Machine

```
UNKNOWN  ──(world load, hash matches)──▶  CLEAN
CLEAN    ──(hash mismatch)─────────────▶  TAMPERED_LEVEL_DAT
CLEAN    ──(death seal tampering)───────▶  TAMPERED_DEATH_LOG
CLEAN    ──(tick rollback)──────────────▶  ROLLBACK_DETECTED
CLEAN    ──(LAN cheats enabled)─────────▶  LAN_CHEAT_DETECTED
*        ──(any of the above)───────────▶  COMPROMISED  (generic alias)
```

Once compromised, `ReadOnlyWorldEnforcer` prevents all block mutations if
`read_only_on_external_cheat_flag = true`.

---

## Known Limitations / Deferred Items

- **Verification API** (`verification_api_port`): HTTP endpoint for external audit queries
  is not implemented in MVP.  Port config key is reserved for Phase 2.
- **Ed25519 signing**: Death seals currently use SHA-256 checksums only.  Asymmetric
  signing (Ed25519) is deferred to Phase 2 for non-repudiation.
- **Full inventory provenance** (`strict_inventory_provenance`): Item-level tracking
  (who crafted/picked up each item) is a no-op in MVP.
- **Merkle tracking integration**: The existing `ChunkHasher` / `NotaryBlock` Merkle
  system is not yet wired into HIK's per-chunk integrity checks.
- **Archive automation**: `ArchiveCountdown` logs "archive pending" but does not yet
  copy the world directory to `archive_path`.  Automation is Phase 2.
- **`SpectatorLockHandler` persistence**: The in-memory lock set is rebuilt from
  `DeathSealManager.isSealed()` checks on login; explicit startup hydration is Phase 2.

---

## MVP Scope

The MVP delivers:
1. Cryptographic death sealing with tamper-detectable checksums
2. `level.dat` hash comparison on every world load
3. Game-tick rollback detection (every 30 s)
4. Per-tick spectator mode enforcement + command interception
5. LAN cheat detection and read-only enforcement
6. Client HUD overlay showing integrity state
7. Forge config file with all tunable knobs
8. Mixin to disable "Allow Cheats" on the LAN share screen for Hardcore worlds
