package io.github.lianjordaan.skyMines.subcommands;

import com.ibm.icu.text.Edits;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import io.github.lianjordaan.skyMines.utils.WorldEditUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CreateSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public CreateSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /skymines create <mineId>");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can select mines."));
            return true;
        }
        Player player = (Player) sender;

        MineManager mineManager = this.mineManager;
        if (mineManager.getMine(args[1]) != null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine <gold>" + args[1] + " <red>already exists."));
            return true;
        }
        WorldEditPlugin worldEditPlugin = WorldEditPlugin.getInstance();
        if (worldEditPlugin.getSession(player).getSelection() == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must select a region first."));
            return true;
        }
        Location minCorner = WorldEditUtils.convertBlockToLocation(worldEditPlugin.getSession(player).getSelection().getMinimumPoint());
        Location maxCorner = WorldEditUtils.convertBlockToLocation(worldEditPlugin.getSession(player).getSelection().getMaximumPoint());

        minCorner.setWorld(player.getWorld());
        maxCorner.setWorld(player.getWorld());

        Mine mine = new Mine(args[1], minCorner, maxCorner);
        Map<Material, Double> oreDistribution = mine.getOreDistribution();
        oreDistribution.put(Material.STONE, 100.0);
        mine.setOreDistribution(oreDistribution);
        mineManager.registerMine(mine);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mine <gold>" + args[1] + " <green>has been created."));





        return true;
    }
}
