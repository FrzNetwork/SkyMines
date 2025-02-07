package io.github.lianjordaan.skyMines.subcommands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import io.github.lianjordaan.skyMines.utils.WorldEditUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public ResetSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /skymines reset <mineId>");
            return true;
        }

        MineManager mineManager = this.mineManager;
        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine <gold>" + args[1] + " <red>does not exist."));
            return true;
        }
        mine.resetMine();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mine <gold>" + args[1] + " <green>has been reset."));
        return true;
    }
}
