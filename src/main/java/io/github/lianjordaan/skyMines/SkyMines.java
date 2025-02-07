package io.github.lianjordaan.skyMines;

import io.github.lianjordaan.skyMines.events.BreakBlockListener;
import io.github.lianjordaan.skyMines.mines.MineManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyMines extends JavaPlugin {
    public MineManager mineManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // create a mine manager
        this.mineManager = new MineManager(this);
        // Register commands
        getCommand("skymines").setExecutor(new SkyMinesCommand(this, mineManager));

        // load all mines
        this.mineManager.loadMines();

        // Register events
        getServer().getPluginManager().registerEvents(new BreakBlockListener(this, mineManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
