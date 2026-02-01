package com.mattwithabat.simplehomes;

import com.mattwithabat.simplehomes.data.Home;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final SimpleHomes plugin;
    private final Map<UUID, PendingTeleport> pendingTeleports;

    public TeleportManager(SimpleHomes plugin) {
        this.plugin = plugin;
        this.pendingTeleports = new HashMap<>();
    }

    public void startTeleport(Player player, Home home) {
        UUID uuid = player.getUniqueId();

        if (pendingTeleports.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "Already teleporting!");
            return;
        }

        int warmupTime = plugin.getConfig().getInt("warmup-time", 3);
        if (warmupTime <= 0) {
            teleport(player, home);
            return;
        }

        String msg = plugin.getConfig().getString("messages.teleport-start")
                .replace("{seconds}", String.valueOf(warmupTime));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        playSound(player, "sound-teleport-start");

        PendingTeleport pending = new PendingTeleport(uuid, home, player.getLocation());
        pendingTeleports.put(uuid, pending);

        new BukkitRunnable() {
            int countdown = warmupTime;

            @Override
            public void run() {
                if (!player.isOnline() || !pendingTeleports.containsKey(uuid)) {
                    cancel();
                    return;
                }

                if (countdown <= 0) {
                    cancel();
                    teleport(player, home);
                    pendingTeleports.remove(uuid);
                    return;
                }

                if (plugin.getConfig().getBoolean("cancel-on-move") && !player.getLocation().getWorld().equals(pending.startLoc.getWorld())) {
                    cancel(player, "moved");
                    return;
                }

                if (plugin.getConfig().getBoolean("cancel-on-move") && player.getLocation().distance(pending.startLoc) > 0.5) {
                    cancel(player, "moved");
                    return;
                }

                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void teleport(Player player, Home home) {
        Location loc = home.getLocation();
        if (loc == null || loc.getWorld() == null) {
            player.sendMessage(ChatColor.RED + "Home location is invalid");
            return;
        }

        player.teleport(loc);
        playSound(player, "sound-teleport-complete");

        String msg = plugin.getConfig().getString("messages.teleported")
                .replace("{name}", home.getName());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void cancel(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        pendingTeleports.remove(uuid);

        String msg = plugin.getConfig().getString("messages.teleport-cancelled");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        playSound(player, "sound-teleport-cancel");
    }

    public boolean isTeleporting(UUID uuid) {
        return pendingTeleports.containsKey(uuid);
    }

    public void onDamage(Player player) {
        if (plugin.getConfig().getBoolean("cancel-on-damage")) {
            cancel(player, "damage");
        }
    }

    private void playSound(Player player, String configKey) {
        String soundName = plugin.getConfig().getString(configKey);
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void shutdown() {
        pendingTeleports.clear();
    }

    private static class PendingTeleport {
        final UUID player;
        final Home home;
        final Location startLoc;

        PendingTeleport(UUID player, Home home, Location startLoc) {
            this.player = player;
            this.home = home;
            this.startLoc = startLoc;
        }
    }
}
