package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;


public class BlockExpDropListener implements Listener {

    private final Set<Material> doubledBlocks = new HashSet<>();

    /**
     * Initializes blocks that can be doubled.
     */
    public BlockExpDropListener() {

        String[] doubledBlockKeys = {"AMETHYST_CLUSTER", "COAL_ORE", "COPPER_ORE", "DEEPSLATE_COAL_ORE",
                "DEEPSLATE_COPPER_ORE", "DEEPSLATE_DIAMOND_ORE", "DEEPSLATE_EMERALD_ORE", "DEEPSLATE_GOLD_ORE",
                "DEEPSLATE_IRON_ORE", "DEEPSLATE_LAPIS_ORE", "DEEPSLATE_REDSTONE_ORE", "DIAMOND_ORE", "EMERALD_ORE",
                "GILDED_BLACKSTONE", "GOLD_ORE", "GRAVEL", "IRON_ORE", "LAPIS_ORE", "NETHER_GOLD_ORE", "REDSTONE_ORE"};
        for(String key : doubledBlockKeys) {
            Material m = Material.getMaterial(key);
            if (m != null) doubledBlocks.add(m);
        }

        Material quartz = Material.getMaterial("NETHER_QUARTZ_ORE");
        if (quartz == null) quartz = Material.getMaterial("QUARTZ_ORE");
        doubledBlocks.add(quartz);
    }

    /**
     * Handles experience amounts and ore doubling when a block is broken.
     *
     * @param event the block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Difficulty d = DifficultyHandler.getPlayerDifficulty(p);

        // Checks if ore experience multiplier is enabled and calculates experience.
        if (ConfigHandler.getToggle("oreExpMod")) {
            event.setExpToDrop((int) Math.round(event.getExpToDrop() * d.getOreExpMod()));
        }

        Block b = event.getBlock();
        Material m = b.getType();
        // If ore doubling is disabled, the block is not valid, or the random check fails, don't do ore doubling.
        if (!ConfigHandler.getToggle("oreLootChance") || !doubledBlocks.contains(m) ||
                IDifficulty.getRand().nextDouble() >= d.getOreLootChance()) return;

        // Gets list of drops to double. The method Block.getDrops(tool, entity) does not exist in all versions.
        ItemStack tool = p.getInventory().getItemInMainHand();
        ItemStack[] drops = b.getDrops(tool).toArray(new ItemStack[0]);

        // Sets block to air to stop normal drops.
        b.setType(Material.AIR);
        // Checks if the drops should be doubled (only when a block is not dropping itself).
        boolean isDoubled = drops.length != 1 || drops[0].getType() != m;
        for (ItemStack i : drops) {
            b.getWorld().dropItemNaturally(b.getLocation(), i);
            if (isDoubled) b.getWorld().dropItemNaturally(b.getLocation(), i);
        }
    }

    /**
     * When items are extracted from a furnace, this method checks if the ore experience modifier is enabled and
     * calculates the resulting experience.
     *
     * @param event the furnace extraction event
     */
    @EventHandler
    public void onBlockExp(FurnaceExtractEvent event) {
        if (ConfigHandler.getToggle("oreExpMod"))
            event.setExpToDrop((int) Math.round(event.getExpToDrop() *
                    DifficultyHandler.getPlayerDifficulty(event.getPlayer()).getOreExpMod()));
    }

}
