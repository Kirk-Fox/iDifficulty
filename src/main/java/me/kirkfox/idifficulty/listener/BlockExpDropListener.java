package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockExpDropListener implements Listener {

    private final List<Material> doubledBlocks = new ArrayList<>();

    /**
     * Initializes blocks that can be doubled.
     */
    public BlockExpDropListener() {
        // These blocks exist in all valid versions.
        doubledBlocks.addAll(Arrays.asList(Material.COAL_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
                Material.GRAVEL, Material.IRON_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE));

        // In 1.13, the ID for quartz ore was changed. This handles the change.
        try {
            doubledBlocks.add(Material.NETHER_QUARTZ_ORE);
        } catch (NoSuchFieldError e) {
            doubledBlocks.add(Material.getMaterial("QUARTZ_ORE"));
        }

        // These blocks were only added in 1.16.
        try {
            doubledBlocks.addAll(Arrays.asList(Material.GILDED_BLACKSTONE, Material.NETHER_GOLD_ORE));
        } catch (NoSuchFieldError ignored) {}

        // These blocks were only added in 1.17.
        try {
            doubledBlocks.addAll(Arrays.asList(Material.AMETHYST_CLUSTER, Material.COPPER_ORE, Material.DEEPSLATE_COAL_ORE,
                    Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
                    Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_LAPIS_ORE,
                    Material.DEEPSLATE_REDSTONE_ORE));
        } catch (NoSuchFieldError ignored) {}
    }

    /**
     * Handles experience amounts and ore doubling when a block is broken.
     *
     * @param event the block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        PlayerDifficulty d = DifficultyHandler.getPlayerDifficulty(p);

        // Checks if ore experience multiplier is enabled and calculates experience.
        if (ConfigHandler.getToggle("oreExpMod")) {
            event.setExpToDrop((int) Math.round(event.getExpToDrop() * d.getOreExpMod()));
        }

        Block b = event.getBlock();
        Material m = b.getType();
        // If ore doubling is disabled, the block is not valid, or the random check fails, don't do ore doubling.
        if (!ConfigHandler.getToggle("oreLootChance") || !doubledBlocks.contains(m) ||
                IDifficulty.getRand().nextDouble() >= d.getOreLootChance()) return;

        // Disables event's item dropping to double later. This does not work on versions prior to 1.12.
        try {
            event.setDropItems(false);
        } catch (NoSuchMethodError e) {
            ConfigHandler.disableOreDoubling();
            return;
        }

        // Gets list of drops to double. The method Block.getDrops(tool, entity) does not exist in all versions.
        ItemStack tool = p.getInventory().getItemInMainHand();
        ItemStack[] drops;
        try {
            drops = b.getDrops(tool, p).toArray(new ItemStack[0]);
        } catch (NoSuchMethodError e) {
            drops = b.getDrops(tool).toArray(new ItemStack[0]);
        }

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
