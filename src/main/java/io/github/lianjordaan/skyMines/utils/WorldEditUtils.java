package io.github.lianjordaan.skyMines.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldEditUtils {
    public static Location convertBlockToLocation(BlockVector3 block) {
        int x = block.x();
        int y = block.y();
        int z = block.z();
        return new Location(null, x, y, z);
    }

    public static BlockVector3 convertLocationToBlockVector(Location location) {
        return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Region createCuboidRegion(World world, Location minCorner, Location maxCorner) {
        // Create a CuboidRegion using the world and corner points

        return new CuboidRegion(BukkitAdapter.adapt(world), convertLocationToBlockVector(minCorner), convertLocationToBlockVector(maxCorner));
    }
}
