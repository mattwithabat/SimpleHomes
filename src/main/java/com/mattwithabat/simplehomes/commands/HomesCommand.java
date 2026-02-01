package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import com.mattwithabat.simplehomes.data.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomesCommand implements CommandExecutor {
    private final SimpleHomes plugin;

    public HomesCommand(SimpleHomes plugin) {
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

        List<Home> homes = plugin.getHomeManager().getPlayerHomes(player.getUniqueId());

        if (homes.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.no-homes")));
            return true;
        }

        int limit = plugin.getConfig().getInt("default-limit", 5);
        int count = homes.size();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.homes-header")
                .replace("{count}", String.valueOf(count))
                .replace("{limit}", String.valueOf(limit))));

        for (Home home : homes) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.homes-format").replace("{name}", home.getName())));
        }

        return true;
    }
}
