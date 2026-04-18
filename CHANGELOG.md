# Changelog

All notable changes to this project will be documented in this file.

## [1.0.1] - 2026-04-18

### Added

- **Spell Binding EMI Integration**: Complete compatibility layer for the Spell Binding Table.
- **Tier Visualization**: Roman numerals (I, II, III+) displayed for each spell tier.
- **Experience Requirement**: Hover tooltip on XP cost showing the minimum level required to bind.
- **XP Cost Display**: Spending cost shown in green directly under the recipe arrow.
- **Lapis Requirements**: Accurate Lapis Lazuli counts per tier.
- **Bookshelf Requirements**: Dynamic bookshelf counter in the bottom-right corner with a hover tooltip.
- **Tab Priority**: Hard-forced the Spell Binding tab to appear first in the EMI recipe viewer.
- **Config Integration**: Automatic synchronization with `SpellEngineMod.config` for level and lapis multipliers.

### Changed

- Improved performance by caching spell and tier data.
- Standardized UI to use Mojang (Official) mappings for better compatibility.
