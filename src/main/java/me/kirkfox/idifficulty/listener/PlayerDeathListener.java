package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Difficulty d = DifficultyHandler.getPlayerDifficulty(p);
        boolean keepInv = d.getKeepInv();
        boolean keepExp = d.getKeepExp();

        if(ConfigHandler.getToggle("keepInv")) {
            event.setKeepInventory(keepInv);
            List<ItemStack> drops = event.getDrops();
            drops.clear();
            if(!keepInv) {
                ItemStack[] inv = p.getInventory().getContents();
                Enchantment vanish = null;
                boolean vanishingCurse = true;
                try {
                    vanish = Enchantment.VANISHING_CURSE;
                } catch (NoSuchFieldError e) {
                    vanishingCurse = false;
                }
                for(ItemStack i : inv) {
                    if(i != null && !(vanishingCurse && i.containsEnchantment(vanish))) {
                        drops.add(i);
                    }
                }
            }
        }

        if(ConfigHandler.getToggle("keepExp")) {
            event.setKeepLevel(keepExp);
            event.setDroppedExp(keepExp ? 0 : Math.min(7*p.getLevel(), 100));
        }

        if(EntityDamageListener.isDyingFromStarvation(p)) {
            event.setDeathMessage(p.getName() + " starved to death");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if(!DifficultyHandler.getPlayerDifficulty(p).getKeepExp()) {
            p.setLevel(0);
            p.setExp(0);
        }
    }

}
