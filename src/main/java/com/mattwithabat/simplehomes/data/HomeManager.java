package com.mattwithabat.simplehomes.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mattwithabat.simplehomes.SimpleHomes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class HomeManager {
    private final SimpleHomes plugin;
    private final File dataFile;
    private final Gson gson;
    private Map<UUID, List<Home>> homes;

    public HomeManager(SimpleHomes plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "homes.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    public List<Home> getPlayerHomes(UUID uuid) {
        return homes.getOrDefault(uuid, new ArrayList<>());
    }

    public Home getHome(UUID uuid, String name) {
        List<Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.stream().filter(h -> h.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean setHome(UUID uuid, String name, Location location) {
        int limit = getHomeLimit(uuid);
        List<Home> playerHomes = homes.computeIfAbsent(uuid, k -> new ArrayList<>());

        Optional<Home> existing = playerHomes.stream().filter(h -> h.getName().equalsIgnoreCase(name)).findFirst();
        if (existing.isPresent()) {
            playerHomes.remove(existing.get());
            playerHomes.add(new Home(name, location));
            return true;
        }

        if (playerHomes.size() >= limit) {
            return false;
        }

        playerHomes.add(new Home(name, location));
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        List<Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return false;
        return playerHomes.removeIf(h -> h.getName().equalsIgnoreCase(name));
    }

    public int getHomeCount(UUID uuid) {
        return homes.getOrDefault(uuid, new ArrayList<>()).size();
    }

    private int getHomeLimit(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null && player.hasPermission("simplehomes.limit.*")) {
            return Integer.MAX_VALUE;
        }
        for (int i = 100; i >= 1; i--) {
            if (player != null && player.hasPermission("simplehomes.limit." + i)) {
                return i;
            }
        }
        return plugin.getConfig().getInt("default-limit", 5);
    }

    public void save() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(homes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        if (!dataFile.exists()) {
            homes = new HashMap<>();
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, List<Home>>>(){}.getType();
            homes = gson.fromJson(reader, type);
            if (homes == null) homes = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            homes = new HashMap<>();
        }
    }
}
