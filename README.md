# Protected Items

Lightweight Paper plugin that adds a command to protect any item in a player’s hand. Protected items cannot be dropped, lost on death, used with right‑click, placed as blocks, or stored in regular containers (but can be stored in ender chests).

## Requirements

- **Paper** 1.21 or newer (or compatible forks)
- Java 21

## Installation

1. Download the latest JAR from [Releases](https://github.com/...) or build it yourself: `gradle build`
2. Put `protected-items-0.1.0.jar` into your server’s `plugins` folder
3. Restart the server or run `paper load protected-items`

## Usage

### Commands

| Command | Description |
|--------|-------------|
| `/protected add` | Mark the item in your main hand as protected |
| `/protected remove` | Remove protection from the item in your main hand |
| `/protected check` | Check whether the item in your main hand is protected |

The command can only be used by a player, and the item must be in the main hand.

### Protected item behavior

- **Drop** — attempts to drop a protected item are cancelled and a message is shown.
- **Death** — protected items are not dropped on death; they are restored to the inventory after respawn. If there is no free space, they are dropped near the player.
- **Block placement** — placing blocks with a protected item in hand is cancelled.
- **Right‑click use** — right‑click usage of protected items (eggs, snowballs, spawn eggs, etc.) is cancelled.
- **Containers** — protected items cannot be moved into regular containers (chests, barrels, etc.), but **can** be stored in ender chests. Operators (or anyone with the bypass permission) can still place them in containers to give them to players.
- **Item frames** — protected items cannot be put into item frames.

The protection flag is stored in the item’s Persistent Data Container (PDC), so it survives restarts and moving items between inventories.

## Building from source

```bash
gradle build
```

The JAR will be in `build/libs/`.

## Testing

Unit tests use JUnit 4 and [MockBukkit](https://github.com/MockBukkit/MockBukkit) (mock Bukkit server, no real Minecraft needed).

Run tests:

```bash
gradle test
```

Coverage:
- **MessagesTest** — localization (ru/en), locale and key fallbacks.
- **IndestructibleUtilTest** — setting/clearing the protected flag in PDC, edge cases (null, air).
- **IndestructibleCommandTest** — permissions, empty hand, add/remove/check, tab completion.

### Manual testing checklist

**Commands**
- **/protected add / remove / check**: work as described, with correct messages and permissions.

**Player (no operator permissions)**
- **Ender chest**: a protected item **can** be placed into an ender chest.
- **Other containers**: a protected item **cannot** be placed into any other storage (chests, barrels, etc.).
- **Death**: a protected item is **not dropped on death** and is restored to the inventory after respawn (or dropped near the player if the inventory is full).
- **Placement & right‑click**: a protected item **cannot** be placed as a block or used with right‑click (eggs, snowballs, spawn eggs, etc.).
- **Item frames**: a protected item **cannot** be put into an item frame.
- **/help**: `/help protected` works and shows correct, localized information.
- **Drop**: a protected item **cannot** be dropped from the inventory.

**Operator (or players with permissions)**
- **Storage bypass**: a protected item **can** be placed into any container (chests, barrels, etc.) when the player has the bypass permission.
- **All other behavior**: matches the player behavior above (no dropping, no usage, protection on death, etc.).

## License

MIT
