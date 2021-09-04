package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.command.DifficultyCommand;
import me.kirkfox.idifficulty.command.DifficultyTabCompleter;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.DifficultyStorage;
import me.kirkfox.idifficulty.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public final class IDifficulty extends JavaPlugin {

    private static IDifficulty plugin;
    private static Random rand;

    private String updateString = null;

    private static final int RESOURCE_ID = 95730;

    @Override
    public void onEnable() {

        plugin = this;
        rand = new Random();

        ConfigHandler.registerConfig(this);
        DifficultyHandler.registerDifficulties();

        registerCommand();
        registerListeners();

        try {
            DifficultyStorage.loadDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new MetricsHandler();

        checkForUpdates();

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
        Listener[] listeners = {new BlockBreakListener(), new DifficultyChangeListener(), new EntityDamageByEntityListener(),
                new EntityDamageListener(), new EntityDeathListener(), new EntityRegainHealthListener(),
                new FoodLevelChangeListener(), new FurnaceExtractListener(), new PlayerDeathListener(),
                new PlayerJoinListener(updateString)};
        for(Listener l : listeners) {
            getServer().getPluginManager().registerEvents(l, this);
        }
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openStream();
                Scanner scanner = new Scanner(inputStream)) {
                if(scanner.hasNext()) {
                    String version = scanner.next();
                    if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                        updateString = "A new version of iDifficulty is available. " +
                                "Go to https://www.spigotmc.org/resources/idifficulty.95730/ for iDifficulty v" + version;
                        outputLog(updateString);
                    }
                }
            } catch (IOException e) {
                outputLog("Cannot look for updates: " + e.getMessage());
            }
        });
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
