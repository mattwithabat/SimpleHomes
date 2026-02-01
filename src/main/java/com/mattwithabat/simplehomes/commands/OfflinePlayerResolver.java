package com.mattwithabat.simplehomes.commands;

import com.mattwithabat.simplehomes.SimpleHomes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class OfflinePlayerResolver {
    private final SimpleHomes plugin;
    private final String playerName;

    public OfflinePlayerResolver(SimpleHomes plugin, String playerName) {
        this.plugin = plugin;
        this.playerName = playerName;
    }

    public UUID getUUID() {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        if (offline != null && offline.hasPlayedBefore()) {
            return offline.getUniqueId();
        }

        File usercacheFile = new File(Bukkit.getServer().getWorldContainer(), "usercache.json");
        if (usercacheFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(usercacheFile);
                for (String key : config.getKeys(false)) {
                    if (key.equalsIgnoreCase(playerName)) {
                        String uuidStr = config.getString(key + ".uuid");
                        if (uuidStr != null) {
                            return UUID.fromString(uuidStr);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }
}
