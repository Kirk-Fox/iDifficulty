package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Difficulty d = DifficultyHandler.getPlayerDifficulty(p);
        boolean keepInv = d.getKeepInv();
        boolean keepExp = d.getKeepExp();

        if(ConfigHandler.getToggle("keepInv")) {
            e.setKeepInventory(keepInv);
            List<ItemStack> drops = e.getDrops();
            drops.clear();
            if(!keepInv) {
                ItemStack[] inv = p.getInventory().getContents();
                for(ItemStack i : inv) {
                    if(i != null && !i.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                        drops.add(i);
                    }
                }
            }
        }

        if(ConfigHandler.getToggle("keepExp")) {
            e.setKeepLevel(keepExp);
            e.setDroppedExp(keepExp ? 0 : Math.min(7*p.getLevel(), 100));
        }

        if(EntityDamageListener.isDyingFromStarvation(p)) {
            e.setDeathMessage(p.getName() + " starved to death");
        }
    }

}
