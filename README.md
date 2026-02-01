# SimpleHomes

No-frills teleportation plugin with per-player homes, warmup timers, and cross-world support.

## Features

- Multiple homes per player
- Configurable warmup timers
- Cancel on move/damage
- Cross-world support
- Permissions-based limits
- Admin commands

## Installation

1. Download the latest `.jar` file
2. Place it in your server's `plugins` folder
3. Restart your server

## Commands

| Command | Aliases | Description |
|---------|---------|-------------|
| `/home <name>` | `/h` | Teleport to a home |
| `/sethome [name]` | `/sh` | Set a home |
| `/delhome <name>` | - | Delete a home |
| `/homes` | `/listhomes` | List your homes |
| `/homeadmin` | - | Admin commands |

## Permissions

| Permission | Description |
|------------|-------------|
| `simplehomes.use` | Use basic home commands |
| `simplehomes.limit.<n>` | Limit homes to n |
| `simplehomes.admin` | Access admin commands |

## Configuration

Edit `plugins/SimpleHomes/config.yml`:
- `warmup-time`: Seconds before teleport
- `cancel-on-move`: Cancel if player moves
- `cancel-on-damage`: Cancel if player takes damage
- `default-limit`: Default max homes
- Messages and sounds

## License

MIT License
