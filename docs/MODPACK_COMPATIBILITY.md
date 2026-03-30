# HIK Modpack Compatibility

## LAN screen mixin

HIK applies a single client-side mixin to `ShareToLanScreen` in order to disable
the vanilla **Allow Cheats** toggle for Hardcore worlds.

### Conflict strategy

- `LanMenuMixin` uses `@Inject(at = @At("RETURN"))`, not `@Overwrite`
- Mixin priority is lowered to `900` so other mods can build the final button list first
- `MixinConfigPlugin` skips the mixin entirely if the target screen is unavailable

### Button matching

The filter is intentionally narrow and only disables labels containing both
`allow` and `cheat` so unrelated LAN buttons remain untouched.

### Manual compatibility checks recommended

Before shipping a pack, verify LAN screen behaviour alongside any mod that also
patches the pause/LAN UI, especially:

- Essential
- LAN World Plug-n-Play
- Any custom menu overhaul mod
