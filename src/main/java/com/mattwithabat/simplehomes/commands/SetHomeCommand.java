package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final SimpleHomes plugin;

    public SetHomeCommand(SimpleHomes plugin) {
        this.plugin = plugin;
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

        String name = args.length > 0 ? args[0] : "home";

        if (!name.matches("[a-zA-Z0-9_-]+")) {
            player.sendMessage(ChatColor.RED + "Home names can only contain letters, numbers, underscores, and hyphens");
            return true;
        }

        boolean success = plugin.getHomeManager().setHome(player.getUniqueId(), name, player.getLocation());

        if (!success) {
            int count = plugin.getHomeManager().getHomeCount(player.getUniqueId());
            int limit = plugin.getConfig().getInt("default-limit", 5);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.home-limit-reached")
                    .replace("{limit}", String.valueOf(limit))));
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.home-set").replace("{name}", name)));

        return true;
    }
}
