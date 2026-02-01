package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import com.mattwithabat.simplehomes.data.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {
    private final SimpleHomes plugin;
    private final Map<UUID, Long> cooldowns;

    public HomeCommand(SimpleHomes plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("simplehomes.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /home <name>");
            return true;
        }

        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), args[0]);
        if (home == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.home-not-found").replace("{name}", args[0])));
            return true;
        }

        long cooldown = plugin.getConfig().getLong("teleport-cooldown", 0) * 1000;
        if (cooldown > 0) {
            Long lastUse = cooldowns.get(player.getUniqueId());
            if (lastUse != null && System.currentTimeMillis() - lastUse < cooldown) {
                long remaining = (cooldown - (System.currentTimeMillis() - lastUse)) / 1000;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.cooldown").replace("{seconds}", String.valueOf(remaining))));
                return true;
            }
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        plugin.getTeleportManager().startTeleport(player, home);

        return true;
    }
}
