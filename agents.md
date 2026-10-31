# Project Guidelines

## Mod Changes and Release Documentation

- For every change to mod code, assets, or behavior, update `README.md` and `CURSEFORGE.md` in the same change so their feature descriptions and release notes remain accurate.
- Bump `mod_version` in `gradle.properties` for each mod change, following Semantic Versioning. Keep version references in the README, CurseForge description, changelog, and packaged artifact instructions consistent with that value.
- Maintain `CHANGELOG.md`: add an entry for every mod change under the new version, grouped under the appropriate Keep a Changelog headings (`Added`, `Changed`, `Fixed`, or `Removed`). Do not replace or discard previous release entries.
- Before finishing, check these files together for stale descriptions, mismatched versions, and missing changelog entries.

## Minecraft 26.3+ (1.21.4+) Item Model and Icon Architecture

- **Mandatory `assets/<namespace>/items/<item_id>.json` Definitions**:
  - In Minecraft 26.3 / 1.21.4+, item model resolution requires an item definition in `assets/<namespace>/items/<item_id>.json`.
  - For **every** newly added item or block item, you MUST create:
    1. `assets/<namespace>/items/<item_id>.json` (linking to the item model, e.g. `{"model": {"type": "minecraft:model", "model": "<namespace>:item/<item_id>"}}`).
    2. `assets/<namespace>/models/item/<item_id>.json` (parent `minecraft:item/generated` with layer texture, or block model parent).
    3. `assets/<namespace>/textures/item/<item_id>.png` (the actual sprite texture).
  - Never omit `assets/<namespace>/items/<item_id>.json`. Without it, items fail to resolve their model, resulting in missing or invisible held items and broken inventory GUI icons.
