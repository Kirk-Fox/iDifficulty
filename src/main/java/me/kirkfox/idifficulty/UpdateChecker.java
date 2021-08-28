package me.kirkfox.idifficulty;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

public class UpdateChecker {

    private JavaPlugin plugin;
    private String version;



    UpdateChecker(JavaPlugin plugin, final Consumer<String> consumer) {
        this.plugin = plugin;
        this.version = plugin.getDescription().getVersion();
    }

}
