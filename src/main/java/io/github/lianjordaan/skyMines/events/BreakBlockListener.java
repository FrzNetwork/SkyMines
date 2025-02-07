package io.github.lianjordaan.skyMines.events;

import io.github.lianjordaan.skyMines.SkyMines;
import io.github.lianjordaan.skyMines.mines.MineManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlockListener implements Listener {
    private final SkyMines plugin;
    private final MineManager mineManager;

    public BreakBlockListener(SkyMines plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakBlock(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("restrict-block-breaks", true)) {
            if (mineManager.getMineAtLocation(event.getBlock().getLocation()) != null) {
                event.setCancelled(true);
            }
        }
    }
}
