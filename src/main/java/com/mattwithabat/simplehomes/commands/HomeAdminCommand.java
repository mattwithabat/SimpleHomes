package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import com.mattwithabat.simplehomes.data.Home;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomeAdminCommand implements CommandExecutor {
    private final SimpleHomes plugin;

    public HomeAdminCommand(SimpleHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simplehomes.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "HomeAdmin Commands:");
            sender.sendMessage(ChatColor.WHITE + "/homeadmin reload" + ChatColor.GRAY + " - Reload config");
            sender.sendMessage(ChatColor.WHITE + "/homeadmin delete <player> <home>" + ChatColor.GRAY + " - Delete a player's home");
            sender.sendMessage(ChatColor.WHITE + "/homeadmin list <player>" + ChatColor.GRAY + " - List player's homes");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded");
                break;

            case "delete":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /homeadmin delete <player> <home>");
                    return true;
                }
                deleteHome(sender, args[1], args[2]);
                break;

            case "list":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /homeadmin list <player>");
                    return true;
                }
                listHomes(sender, args[1]);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand");
        }

        return true;
    }

    private void deleteHome(CommandSender sender, String playerName, String homeName) {
        Player target = Bukkit.getPlayer(playerName);
        UUID uuid;

        if (target != null) {
            uuid = target.getUniqueId();
        } else {
            OfflinePlayerResolver resolver = new OfflinePlayerResolver(plugin, playerName);
            uuid = resolver.getUUID();
            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
        }

        boolean success = plugin.getHomeManager().deleteHome(uuid, homeName);

        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Deleted home '" + homeName + "' for " + playerName);
        } else {
            sender.sendMessage(ChatColor.RED + "Home '" + homeName + "' not found for " + playerName);
        }
    }

    private void listHomes(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        UUID uuid;

        if (target != null) {
            uuid = target.getUniqueId();
        } else {
            OfflinePlayerResolver resolver = new OfflinePlayerResolver(plugin, playerName);
            uuid = resolver.getUUID();
            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
        }

        List<Home> homes = plugin.getHomeManager().getPlayerHomes(uuid);

        if (homes.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + playerName + " has no homes");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + playerName + "'s Homes:");
        for (Home home : homes) {
            sender.sendMessage(ChatColor.WHITE + "- " + home.getName() + " (" + home.getWorld() + ")");
        }
    }
}
