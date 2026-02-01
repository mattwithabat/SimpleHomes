package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final SimpleHomes plugin;

    public DelHomeCommand(SimpleHomes plugin) {
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

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /delhome <name>");
            return true;
        }

        boolean success = plugin.getHomeManager().deleteHome(player.getUniqueId(), args[0]);

        if (!success) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.home-not-found").replace("{name}", args[0])));
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.home-deleted").replace("{name}", args[0])));

        return true;
    }
}
