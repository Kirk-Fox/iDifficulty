package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class FurnaceExtractListener implements Listener {

    @EventHandler
    public void onBlockExp(FurnaceExtractEvent e) {
        if(ConfigHandler.getToggle("expMod")) {
            e.setExpToDrop((int) Math.round(e.getExpToDrop() * DifficultyHandler.getPlayerDifficulty(e.getPlayer()).getExpMod()));
        }
    }

}
