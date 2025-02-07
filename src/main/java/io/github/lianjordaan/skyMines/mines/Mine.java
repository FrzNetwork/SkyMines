package io.github.lianjordaan.skyMines.mines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import io.github.lianjordaan.skyMines.utils.WorldEditUtils;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class Mine {
    private String id;
    private String name;
    private Location minCorner;
    private Location maxCorner;
    private Map<Material, Double> oreDistribution;
    private int resetTime;
    private boolean privateMine;
    private Player owner;
    private long lastReset;
    private long resetPercentage;

    public Mine(String id, Location minCorner, Location maxCorner) {
        this.id = id;
        this.name = id;
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.oreDistribution = new HashMap<>();
        this.resetTime = 60;
        this.privateMine = false;
        this.owner = null;
        this.lastReset = System.currentTimeMillis();
        this.resetPercentage = 0;
    }

    public void resetMine() {
        lastReset = System.currentTimeMillis(); // Update last reset timestamp

        World world = minCorner.getWorld();
        if (world == null) return;

        // Convert ore distribution map into a cumulative probability map
        NavigableMap<Double, Material> weightedMaterials = new TreeMap<>();
        double totalWeight = 0.0;

        for (Map.Entry<Material, Double> entry : oreDistribution.entrySet()) {
            totalWeight += entry.getValue();
            weightedMaterials.put(totalWeight, entry.getKey());
        }

        if (weightedMaterials.isEmpty()) {
            weightedMaterials.put(100.0, Material.STONE); // Default to stone
            totalWeight = 100.0;
        }

        Random random = new Random();

        List<Player> playersInMine = getPlayersInMine();
        if (!playersInMine.isEmpty()) {
            for (Player player : playersInMine) {
                if (wouldSuffocate(player)) {
                    Location teleportLocation = player.getLocation();
                    teleportLocation.setY(maxCorner.getY() + 1);
                    player.teleport(teleportLocation,
                            TeleportFlag.Relative.VELOCITY_X,
                            TeleportFlag.Relative.VELOCITY_Y, TeleportFlag.Relative.VELOCITY_Z,
                            TeleportFlag.Relative.VELOCITY_ROTATION
                    );
                }
            }
        }

        // Loop through the mine area and set blocks using cumulative probability
        for (int x = minCorner.getBlockX(); x <= maxCorner.getBlockX(); x++) {
            for (int y = minCorner.getBlockY(); y <= maxCorner.getBlockY(); y++) {
                for (int z = minCorner.getBlockZ(); z <= maxCorner.getBlockZ(); z++) {
                    double randomValue = random.nextDouble() * totalWeight;
                    Material selectedMaterial = weightedMaterials.ceilingEntry(randomValue).getValue();
                    world.getBlockAt(x, y, z).setType(selectedMaterial);
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getMinPoint() {
        return minCorner;
    }

    public Location getMaxPoint() {
        return maxCorner;
    }

    public Map<Material, Double> getOreDistribution() {
        return oreDistribution;
    }

    public int getResetTime() {
        return resetTime;
    }

    public boolean isPrivateMine() {
        return privateMine;
    }

    public Player getOwner() {
        return owner;
    }

    public long getLastReset() {
        return lastReset;
    }

    public long getResetPercentage() {
        return resetPercentage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinCorner(Location minCorner) {
        this.minCorner = minCorner;
    }

    public void setMaxCorner(Location maxCorner) {
        this.maxCorner = maxCorner;
    }

    public void setOreDistribution(Map<Material, Double> oreDistribution) {
        this.oreDistribution.putAll(oreDistribution);
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public void setPrivateMine(boolean privateMine) {
        this.privateMine = privateMine;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setLastReset(long lastReset) {
        this.lastReset = lastReset;
    }

    public void setResetPercentage(long resetPercentage) {
        this.resetPercentage = resetPercentage;
    }

    // Determines if the mine needs to be reset
    public boolean needsReset() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastReset;

        // Time-based reset check
        if (elapsedTime >= resetTime * 1000L) {
            return true;
        }

        // Percentage-based reset check (if resetPercentage is set)
        if (resetPercentage >= 0) {
            double remainingBlocks = calculateAirBlocksInMain();
            double totalBlocks = (maxCorner.getBlockX() - minCorner.getBlockX() + 1) *
                    (maxCorner.getBlockY() - minCorner.getBlockY() + 1) *
                    (maxCorner.getBlockZ() - minCorner.getBlockZ() + 1);

            remainingBlocks = totalBlocks - remainingBlocks;

            return (remainingBlocks / totalBlocks) * 100 <= resetPercentage;
        }

        return false; // Only reset based on time if resetPercentage is -1
    }

    // Calculate the number of remaining air blocks in the mine
    private double calculateAirBlocksInMain() {
        WorldEditPlugin worldEditPlugin = WorldEditPlugin.getInstance();
        World world = minCorner.getWorld();
        EditSession editSession = worldEditPlugin.getWorldEdit().newEditSession(BukkitAdapter.adapt(world));
        List<Countable<BlockType>> blockDistribution  = editSession.getBlockDistribution(WorldEditUtils.createCuboidRegion(world, minCorner, maxCorner));

        int airBlocks = 0;

        // Iterate through the block distribution and sum air blocks
        for (Countable<BlockType> entry : blockDistribution) {
            BlockType blockType = entry.getID();
            if (blockType.getMaterial().isAir()) {
                airBlocks += entry.getAmount();
            }
        }
        return airBlocks;
    }

    public boolean isInside(Location loc) {
        return loc.getWorld().equals(minCorner.getWorld()) &&
                loc.getBlockX() >= minCorner.getBlockX() && loc.getBlockX() <= maxCorner.getBlockX() &&
                loc.getBlockY() >= minCorner.getBlockY() && loc.getBlockY() <= maxCorner.getBlockY() &&
                loc.getBlockZ() >= minCorner.getBlockZ() && loc.getBlockZ() <= maxCorner.getBlockZ();
    }

    public void normalizeCornerLocations() {
        if (minCorner.getBlockX() > maxCorner.getBlockX()) {
            int temp = minCorner.getBlockX();
            minCorner.setX(maxCorner.getBlockX());
            maxCorner.setX(temp);
        }

        if (minCorner.getBlockY() > maxCorner.getBlockY()) {
            int temp = minCorner.getBlockY();
            minCorner.setY(maxCorner.getBlockY());
            maxCorner.setY(temp);
        }

        if (minCorner.getBlockZ() > maxCorner.getBlockZ()) {
            int temp = minCorner.getBlockZ();
            minCorner.setZ(maxCorner.getBlockZ());
            maxCorner.setZ(temp);
        }
    }

    public boolean wouldSuffocate(Player player) {
        BoundingBox mineBox = new BoundingBox(
                minCorner.getX(), minCorner.getY(), minCorner.getZ(),
                maxCorner.getX() + 1, maxCorner.getY() + 1, maxCorner.getZ() + 1
        );

        return mineBox.overlaps(player.getBoundingBox());
    }

    public List<Player> getPlayersInMine() {
        BoundingBox mineBox = new BoundingBox(
                minCorner.getX(), minCorner.getY(), minCorner.getZ(),
                maxCorner.getX() + 1, maxCorner.getY() + 1, maxCorner.getZ() + 1
        );

        List<Player> playersInMine = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (mineBox.overlaps(player.getBoundingBox())) {
                playersInMine.add(player);
            }
        }
        return playersInMine;
    }
}
