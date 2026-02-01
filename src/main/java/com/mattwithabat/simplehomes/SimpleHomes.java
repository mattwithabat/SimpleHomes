package com.mattwithabat.simplehomes;

import com.mattwithabat.simplehomes.commands.DelHomeCommand;
import com.mattwithabat.simplehomes.commands.HomeAdminCommand;
import com.mattwithabat.simplehomes.commands.HomeCommand;
import com.mattwithabat.simplehomes.commands.HomesCommand;
import com.mattwithabat.simplehomes.commands.SetHomeCommand;
import com.mattwithabat.simplehomes.data.HomeManager;
import com.mattwithabat.simplehomes.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleHomes extends JavaPlugin {

    private static SimpleHomes instance;
    private HomeManager homeManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        homeManager = new HomeManager(this);
        teleportManager = new TeleportManager(this);

        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("homeadmin").setExecutor(new HomeAdminCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("SimpleHomes enabled");
    }

    @Override
    public void onDisable() {
        teleportManager.shutdown();
        homeManager.save();
        getLogger().info("SimpleHomes disabled");
    }

    public static SimpleHomes getInstance() {
        return instance;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
