package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.DifficultyStorage;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;

import java.util.HashMap;
import java.util.Map;

public class MetricsHandler extends Metrics {

    private static final int BSTATS_ID = 12440;

    public MetricsHandler() {
        super(IDifficulty.getPlugin(), BSTATS_ID);

        addCustomChart(new SimplePie("difficultyNumber", () -> String.valueOf(DifficultyHandler.getDifficultyList().size())));
        addCustomChart(new SimplePie("defaultDifficulty", () -> DifficultyHandler.getDefaultDifficulty().getName()));

        for(String key : ConfigHandler.getToggleMap().keySet()) {
            addCustomChart(new SimplePie(key + "Toggle", () -> String.valueOf(ConfigHandler.getToggle(key))));
        }

        addCustomChart(new AdvancedPie("playerDifficultyCount", () -> getPlayerDifficultyCount(true)));
        addCustomChart(new AdvancedPie("playerNonDefaultDifficultyCount", () -> getPlayerDifficultyCount(false)));
    }

    private Map<String, Integer> getPlayerDifficultyCount(boolean includeDefault) {
        Map<String, Integer> dMap = new HashMap<>();
        for (Difficulty d : DifficultyStorage.getPlayerDifficulties()) {
            String name = d.getName();
            if (includeDefault || !name.equalsIgnoreCase(DifficultyHandler.getDefaultDifficulty().getName()))
                dMap.put(name, dMap.getOrDefault(name, 0) + 1);
        }
        return dMap;
    }

}
