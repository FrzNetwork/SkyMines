package io.github.lianjordaan.skyMines.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class SelectSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public SelectSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /skymines select <mineId>");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can select mines."));
            return true;
        }
        Player player = (Player) sender;
        MineManager mineManager = this.mineManager;
        if (mineManager.getMine(args[1]) == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine not found: " + args[1]));
            return true;
        }
        Mine mine = mineManager.getMine(args[1]);
        WorldEditPlugin worldEditPlugin = WorldEditPlugin.getInstance();
        worldEditPlugin.getSession(player).getRegionSelector(BukkitAdapter.adapt(player.getWorld())).selectPrimary(BukkitAdapter.adapt(mine.getMinPoint()).toBlockPoint(), null);
        worldEditPlugin.getSession(player).getRegionSelector(BukkitAdapter.adapt(player.getWorld())).selectSecondary(BukkitAdapter.adapt(mine.getMaxPoint()).toBlockPoint(), null);
        return true;
    }
}
