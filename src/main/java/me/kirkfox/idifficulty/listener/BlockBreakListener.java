package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBreakListener implements Listener {

    private final List<Material> doubledBlocks = new ArrayList<>();

    public BlockBreakListener() {
        // All versions
        doubledBlocks.addAll(Arrays.asList(Material.COAL_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
                Material.GRAVEL, Material.IRON_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE));
        try {
            // 1.13+
            doubledBlocks.add(Material.NETHER_QUARTZ_ORE);
        } catch (NoSuchFieldError e) {
            doubledBlocks.add(Material.getMaterial("QUARTZ_ORE"));
        }
        // 1.16+
        try {
            doubledBlocks.addAll(Arrays.asList(Material.GILDED_BLACKSTONE, Material.NETHER_GOLD_ORE));
        } catch (NoSuchFieldError ignored) {}
        // 1.17+
        try {
            doubledBlocks.addAll(Arrays.asList(Material.AMETHYST_CLUSTER, Material.COPPER_ORE, Material.DEEPSLATE_COAL_ORE,
                    Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
                    Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_LAPIS_ORE,
                    Material.DEEPSLATE_REDSTONE_ORE));
        } catch (NoSuchFieldError ignored) {}
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        PlayerDifficulty d = DifficultyHandler.getPlayerDifficulty(p);

        if(ConfigHandler.getToggle("oreExpMod")) {
            e.setExpToDrop((int) Math.round(e.getExpToDrop() * d.getOreExpMod()));
        }

        Block b = e.getBlock();
        Material m = b.getType();
        if(ConfigHandler.getToggle("oreLootChance") && doubledBlocks.contains(m) &&
                IDifficulty.getRand().nextDouble() < d.getOreLootChance()) {
            ItemStack[] drops = b.getDrops(p.getInventory().getItemInMainHand(), p).toArray(new ItemStack[0]);
            boolean isDoubled = drops.length != 1 || drops[0].getType() != m;
            e.setDropItems(false);
            for (ItemStack i : drops) {
                b.getWorld().dropItemNaturally(b.getLocation(), i);
                if(isDoubled) b.getWorld().dropItemNaturally(b.getLocation(), i);
            }
        }
    }

}
