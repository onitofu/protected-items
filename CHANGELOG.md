# Changelog

## 1.1

### New Commands
- `/protected toggle` — enable or disable item protection globally (requires `protecteditems.admin`). When disabled, all protection rules are suspended — items can be dropped, placed, stored, etc.

### Bug Fixes
- When a player breaks a container (chest, barrel, etc.) holding a protected item, the item now goes directly into the player's inventory instead of dropping on the ground. If the inventory is full, a random non-protected item is displaced and dropped instead.

### Improvements
- Localization messages updated for the new toggle command (English and Russian).

## 1.0

### New Commands
- `/protected list` — view all protected items in your inventory.
- `/protected list <player>` — view protected items in another player's inventory (requires `protecteditems.admin`).
- `/protected listall` — view all protected items across the server with their locations: player inventory, ender chest, or container coordinates (requires `protecteditems.admin`).

### New Permissions
- `protecteditems.admin` — grants access to `/protected list <player>` and `/protected listall`.

### Improvements
- Players are no longer removed from the keep-on-death list if they disconnect before respawning.
- Localization messages updated for all new commands (English and Russian).
