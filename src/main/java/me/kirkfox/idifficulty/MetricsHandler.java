package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDataStorage;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;

public class MetricsHandler extends Metrics {

    private static final int BSTATS_ID = 12440;

    public MetricsHandler(IDifficulty plugin) {
        super(plugin, BSTATS_ID);

        addCustomChart(new SimplePie("difficultyNumber", () -> String.valueOf(DifficultyHandler.getDifficultyNumber())));
        addCustomChart(new SimplePie("defaultDifficulty", () -> DifficultyHandler.getDefaultDifficulty().getName()));

        for(String key : ConfigHandler.getToggleMap().keySet()) {
            addCustomChart(new SimplePie(key + "Toggle", () -> String.valueOf(ConfigHandler.getToggle(key))));
        }

        addCustomChart(new AdvancedPie("playerDifficultyCount", () -> PlayerDataStorage.getPlayerDifficultyCount(true)));
        addCustomChart(new AdvancedPie("playerNonDefaultDifficultyCount", () -> PlayerDataStorage.getPlayerDifficultyCount(false)));
    }

}
