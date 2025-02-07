package io.github.lianjordaan.skyMines.subcommands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
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

import java.util.Map;

public class SetSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public SetSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 4) {
            sender.sendMessage("§cUsage: /skymines set <mineId> <block> <percentage>");
            return true;
        }

        String mineId = args[1];
        String block = args[2];
        String percentage = args[3];

        MineManager mineManager = this.mineManager;
        Mine mine = mineManager.getMine(mineId);
        if (mine == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine <gold>" + mineId + " <red>does not exist."));
            return true;
        }
        Material blockMaterial = Material.matchMaterial(block);
        if (blockMaterial == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid block: " + block));
            return true;
        }
        double percentageDouble = Double.parseDouble(percentage);
        if (percentageDouble < 0 || percentageDouble > 100) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid percentage: " + percentage));
            return true;
        }
        Map<Material, Double> oreDistribution = mine.getOreDistribution();

        if (percentageDouble == 0) {
            oreDistribution.remove(blockMaterial);
            mine.setOreDistribution(oreDistribution);
            mineManager.saveMines();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Block <gold>" + block + " <red>for mine <gold>" + mineId + " <red>has been removed."));
            return true;
        }

        oreDistribution.put(blockMaterial, percentageDouble);

        mine.setOreDistribution(oreDistribution);
        mineManager.saveMines();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Block distribution for block <gold>" + block + " <green> for mine <gold>" + mineId + " <green>has been set to <gold>" + percentage + "%"));

        return true;
    }
}
