package io.github.lianjordaan.skyMines.mines;

import io.github.lianjordaan.skyMines.SkyMines;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MineManager {
    private final SkyMines plugin;
    private final Map<String, Mine> mines = new HashMap<>();
    private final File minesFile;
    private final YamlConfiguration minesConfig;

    public MineManager(SkyMines plugin) {
        this.plugin = plugin;
        this.minesFile = new File(plugin.getDataFolder(), "mines.yml"); // Define custom file
        this.minesConfig = YamlConfiguration.loadConfiguration(minesFile); // Load the mines.yml file

        // Load all mines from the config
        loadMines();

        // Schedule mine resets every second
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAndResetMines, 0L, 200L);
    }

    // Register a mine
    public void registerMine(Mine mine) {
        mines.put(mine.getId(), mine);
        saveMines(); // Save after registering or updating the mine
    }

    // Get a mine by ID
    public Mine getMine(String id) {
        return mines.get(id);
    }

    // Get a mine at location
    public Mine getMineAtLocation(Location location) {
        for (Mine mine : mines.values()) {
            if (mine.isInside(location)) {
                return mine;
            }
        }
        return null;
    }

    // Get all registered mines
    public Map<String, Mine> getAllMines() {
        return mines;
    }

    // Remove a mine
    public void removeMine(String id) {
        mines.remove(id);
        saveMines(); // Save after removing the mine
    }

    // Handle resetting the mine after a set time
    public void resetMines() {
        for (Mine mine : mines.values()) {
            mine.resetMine();
        }
    }

    // Save all mines to the mines.yml file
    public void saveMines() {
        // Clear existing mines data
        minesConfig.set("mines", null);

        for (Mine mine : mines.values()) {
            String path = "mines." + mine.getId();
            minesConfig.set(path + ".name", mine.getName());
            minesConfig.set(path + ".minCorner", serializeLocation(mine.getMinPoint()));
            minesConfig.set(path + ".maxCorner", serializeLocation(mine.getMaxPoint()));

            // Convert ore distribution to a valid YAML format
            Map<String, Double> oreData = new HashMap<>();
            for (Map.Entry<Material, Double> entry : mine.getOreDistribution().entrySet()) {
                oreData.put(entry.getKey().name(), entry.getValue());
            }
            minesConfig.set(path + ".oreDistribution", oreData);

            minesConfig.set(path + ".resetTime", mine.getResetTime());
            minesConfig.set(path + ".privateMine", mine.isPrivateMine());
            if (mine.getOwner() != null) {
                minesConfig.set(path + ".owner", mine.getOwner().getUniqueId().toString());
            } else {
                minesConfig.set(path + ".owner", null);
            }
        }

        try {
            minesConfig.save(minesFile); // Save the data to mines.yml
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save mines data to mines.yml: " + e.getMessage());
        }
    }

    // Load mines from the mines.yml file
    public void loadMines() {
        if (!minesFile.exists()) {
            plugin.saveResource("mines.yml", false); // Create the file if it doesn't exist
        }

        if (!minesConfig.contains("mines")) {
            return; // No mines saved in the file yet
        }

        for (String mineId : minesConfig.getConfigurationSection("mines").getKeys(false)) {
            String path = "mines." + mineId;

            String name = minesConfig.getString(path + ".name");
            Location minCorner = deserializeLocation(minesConfig.getString(path + ".minCorner"));
            Location maxCorner = deserializeLocation(minesConfig.getString(path + ".maxCorner"));

            // Load ore distribution safely
            Map<Material, Double> oreDistribution = new HashMap<>();
            ConfigurationSection oreSection = minesConfig.getConfigurationSection(path + ".oreDistribution");
            if (oreSection != null) {
                for (String key : oreSection.getKeys(false)) {
                    Material material = Material.matchMaterial(key);
                    if (material != null) {
                        oreDistribution.put(material, oreSection.getDouble(key));
                    }
                }
            }

            int resetTime = minesConfig.getInt(path + ".resetTime");
            boolean privateMine = minesConfig.getBoolean(path + ".privateMine");
            Player owner = null;
            if (minesConfig.contains(path + ".owner")) {
                String ownerUUID = minesConfig.getString(path + ".owner");
                if (ownerUUID != null) {
                    owner = Bukkit.getPlayer(UUID.fromString(ownerUUID)); // Get the player from UUID
                }
            }

            Mine mine = new Mine(mineId, minCorner, maxCorner);
            mine.setName(name);
            mine.setOreDistribution(oreDistribution);
            mine.setResetTime(resetTime);
            mine.setPrivateMine(privateMine);
            mine.setOwner(owner);

            mines.put(mineId, mine);
        }
    }

    // Periodically check and reset mines
    private void checkAndResetMines() {
        for (Mine mine : mines.values()) {
            if (mine.needsReset()) {
                mine.resetMine(); // Reset the mine
            }
        }
    }

    // Serialize a Location object to a string (world,x,y,z)
    private String serializeLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    // Deserialize a Location string back to a Location object
    private Location deserializeLocation(String serializedLocation) {
        if (serializedLocation == null) return null;

        String[] parts = serializedLocation.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}
