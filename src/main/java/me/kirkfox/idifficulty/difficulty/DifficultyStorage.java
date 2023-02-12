package me.kirkfox.idifficulty.difficulty;

import com.google.gson.Gson;
import me.kirkfox.idifficulty.IDifficulty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DifficultyStorage {

    private static List<PlayerDifficulty> playerDifficulties = new ArrayList<>();

    @NotNull
    public static PlayerDifficulty createDifficulty(UUID uuid) {
        return createDifficulty(uuid, DifficultyHandler.getDefaultDifficulty());
    }

    @NotNull
    public static PlayerDifficulty createDifficulty(UUID uuid, Difficulty d) {
        PlayerDifficulty pd = new PlayerDifficulty(uuid, d);
        playerDifficulties.add(pd);

        try {
            saveDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pd;
    }

    @Nullable
    public static PlayerDifficulty readDifficulty(UUID uuid) {

        for (PlayerDifficulty d : playerDifficulties) {
            if (d.getUUID().equals(uuid)) {
                return d;
            }
        }
        return null;
    }

    @Nullable
    public static PlayerDifficulty updateDifficulty(UUID uuid, Difficulty newD) {

        for (PlayerDifficulty d : playerDifficulties) {
            if (d.getUUID().equals(uuid)) {

                d.setDifficulty(newD);

                try {
                    saveDifficulties();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return d;
            }
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveDifficulties() throws IOException {
        Gson gson = new Gson();
        File file = new File(IDifficulty.getPlugin().getDataFolder().getAbsolutePath() + "/playerdata.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        FileWriter writer = new FileWriter(file, false);
        gson.toJson(playerDifficulties, writer);
        writer.flush();
        writer.close();
        IDifficulty.outputLog("iDifficulty's player data saved!");
    }

    public static void loadDifficulties() throws IOException {
        Gson gson = new Gson();
        File file = new File(IDifficulty.getPlugin().getDataFolder().getAbsolutePath() + "/playerdata.json");
        if (file.exists()) {
            FileReader reader = new FileReader(file);
            PlayerDifficulty[] d = gson.fromJson(reader, PlayerDifficulty[].class);
            playerDifficulties = new ArrayList<>(Arrays.asList(d));

            IDifficulty.outputLog("iDifficulty's player data loaded!");
        }
    }

    @NotNull
    public static List<PlayerDifficulty> getPlayerDifficulties() {
        return Collections.unmodifiableList(playerDifficulties);
    }

}
