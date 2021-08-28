package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if(!(entity instanceof Player)) {
            Player player = entity.getKiller();
            if(player != null) {
                PlayerDifficulty d = DifficultyHandler.getPlayerDifficulty(player);
                if(ConfigHandler.getToggle("expMod")) {
                    e.setDroppedExp((int) Math.round(e.getDroppedExp() * d.getExpMod()));
                }

                if(ConfigHandler.getToggle("lootChance") && IDifficulty.getRand().nextDouble() < d.getLootChance()) {
                    List<ItemStack> doubledLoot = new ArrayList<>(e.getDrops());
                    EntityEquipment equip = entity.getEquipment();
                    if (equip != null) {
                        for(ItemStack i : equip.getArmorContents()) {
                            doubledLoot.remove(i);
                        }
                        doubledLoot.remove(equip.getItemInMainHand());
                        doubledLoot.remove(equip.getItemInOffHand());
                    }
                    e.getDrops().addAll(doubledLoot);
                }
            }
        }
    }

}
