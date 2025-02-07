package io.github.lianjordaan.skyMines.subcommands;

import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.Mine;
import io.github.lianjordaan.skyMines.mines.MineManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListSubCommand {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public ListSubCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /skymines list");
            return true;
        }
        MineManager mineManager = this.mineManager;
        mineManager.loadMines();
        Map<String, Mine> mineList = mineManager.getAllMines();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mines:"));
        for (Map.Entry<String, Mine> entry : mineList.entrySet()) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>- <gold>" + entry.getKey()));
        }
        return true;
    }
}
