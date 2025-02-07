package io.github.lianjordaan.skyMines.subcommands;

import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class DeleteSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public DeleteSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /skymines delete <mineId>");
            return true;
        }

        String mineId = args[1];

        MineManager mineManager = this.mineManager;
        Mine mine = mineManager.getMine(mineId);
        if (mine == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Mine <gold>" + mineId + " <red>does not exist."));
            return true;
        }
        mineManager.removeMine(mineId);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mine <gold>" + mineId + " <green>has been deleted."));
        return true;
    }
}
