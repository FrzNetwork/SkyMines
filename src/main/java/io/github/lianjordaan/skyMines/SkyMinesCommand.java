package io.github.lianjordaan.skyMines;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.lianjordaan.skyMines.mines.MineManager;
import io.github.lianjordaan.skyMines.subcommands.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyMinesCommand implements CommandExecutor {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public SkyMinesCommand(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skymines.command")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to use this command."));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /skymines help");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
            case "create":
                return new CreateSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "select":
                return new SelectSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "set":
                return new SetSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "list":
                return new ListSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "delete":
                return new DeleteSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "redefine":
                return new RedefineSubCommand(plugin, mineManager).execute(sender, command, label, args);
            case "reset":
                return new ResetSubCommand(plugin, mineManager).execute(sender, command, label, args);
            default:
//                return new HelpSubCommand(plugin, mineManager).execute(sender, command, label, args);
                break;
        }
        return true;
    }
}
