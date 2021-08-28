package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.command.DifficultyCommand;
import me.kirkfox.idifficulty.command.DifficultyTabCompleter;
import me.kirkfox.idifficulty.difficulty.DifficultyStorage;
import me.kirkfox.idifficulty.listener.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public final class IDifficulty extends JavaPlugin {

    private static IDifficulty plugin;
    private static Random rand;

    @Override
    public void onEnable() {

        plugin = this;
        rand = new Random();

        ConfigHandler.registerConfig(this);

        registerCommand();
        registerListeners();

        try {
            DifficultyStorage.loadDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new MetricsHandler();

        outputLog("iDifficulty has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        try {
            DifficultyStorage.saveDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommand() {
        PluginCommand pc = Objects.requireNonNull(getCommand("idifficulty"));
        pc.setExecutor(new DifficultyCommand());
        pc.setTabCompleter(new DifficultyTabCompleter());
    }

    private void registerListeners() {
        Listener[] listeners = {new BlockBreakListener(), new EntityDamageByEntityListener(), new EntityDeathListener(),
                new FurnaceExtractListener(), new PlayerDeathListener()};
        for(Listener l : listeners) {
            getServer().getPluginManager().registerEvents(l, this);
        }
    }

    public static void outputLog(String msg) {
        plugin.getLogger().info(msg);
    }

    public static IDifficulty getPlugin() {
        return plugin;
    }

    public static Player getPlayer(String name) {
        return plugin.getServer().getPlayer(name);
    }

    public static Random getRand() {
        return rand;
    }

}
