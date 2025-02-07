package io.github.lianjordaan.skyMines.subcommands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import io.github.lianjordaan.skyMines.utils.WorldEditUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedefineSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public RedefineSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /skymines redefine <mineId>");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can redefine mines."));
            return true;
        }
        Player player = (Player) sender;

        MineManager mineManager = this.mineManager;
        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine <gold>" + args[1] + " <red>does not exist."));
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

        mine.setMinCorner(minCorner);
        mine.setMaxCorner(maxCorner);

        mineManager.saveMines();

        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mine <gold>" + args[1] + " <green>has been redefined."));
        return true;
    }
}
