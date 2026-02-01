package com.mattwithabat.simplehomes.listeners;

import com.mattwithabat.simplehomes.SimpleHomes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SimpleHomes plugin;

    public PlayerListener(SimpleHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() != event.getTo().getX() ||
            event.getFrom().getZ() != event.getTo().getZ()) {
            plugin.getTeleportManager().cancel(event.getPlayer(), "moved");
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            plugin.getTeleportManager().onDamage((Player) event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTeleportManager().cancel(event.getPlayer(), "quit");
    }
}
