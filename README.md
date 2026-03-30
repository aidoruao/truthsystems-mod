# TruthSystems Mod

See [GITHUB_README.md](GITHUB_README.md) for the full project overview.

## Hardcore Integrity Keeper (HIK)

HIK is the fifth subsystem of TruthSystems — a cryptographic enforcement layer that makes Hardcore mode **truly permanent**.

### What it does

| Feature | Mechanism |
|---|---|
| **Death sealing** | SHA-256 checksummed death records in `<world>/soulbind/<uuid>.json` |
| **Spectator lock** | Locked players are held in spectator mode and cannot change game mode via commands |
| **Archive countdown** | Grace period (`spectator_grace_minutes`) before world is flagged for archival |
| **level.dat tamper detection** | SHA-256 of `level.dat` compared on every server start |
| **Rollback detection** | Game tick persisted every 30 s; backwards tick = rollback flag |
| **LAN cheat blocking** | Mixin disables "Allow Cheats" on `ShareToLanScreen` for Hardcore worlds |
| **Read-only enforcement** | Block-break/place cancelled when world integrity ≠ CLEAN |
| **HUD overlay** | Green `HIK: CLEAN` / red `HIK: <FLAG>` rendered in the top-left corner |

### Configuration

Config file: `config/truthsystems-hik.toml` (auto-generated on first run).

See [docs/HIK_ARCHITECTURE.md](docs/HIK_ARCHITECTURE.md) for full architecture documentation and
[docs/MODPACK_COMPATIBILITY.md](docs/MODPACK_COMPATIBILITY.md) for the LAN-screen mixin compatibility notes.

### Subsystems

- `com.truthsystems.hardcore.soulbind` — death seals & event handling
- `com.truthsystems.hardcore.integrity` — level.dat watching & HUD overlay
- `com.truthsystems.hardcore.backup` — session IDs & rollback detection
- `com.truthsystems.hardcore.spectator` — lock enforcement, countdown, command interception
- `com.truthsystems.hardcore.lan` — LAN mixin, cheat watcher, read-only enforcer
