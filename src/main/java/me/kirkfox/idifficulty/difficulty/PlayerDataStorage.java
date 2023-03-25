package me.kirkfox.idifficulty.difficulty;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.kirkfox.idifficulty.IDifficulty;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage {

    private static final Map<UUID, PlayerDataObject> PLAYER_DIFFICULTY_MAP = new HashMap<>();

    /**
     * Checks the difficulty of the player with the given UUID. If no difficulty is set, sets to default.
     *
     * @param uuid the UUID of the player
     * @return the players difficulty
     */
    @NotNull
    protected static Difficulty readDifficulty(UUID uuid) {
        PlayerDataObject dataObject = PLAYER_DIFFICULTY_MAP.get(uuid);
        if (dataObject == null) {
            PLAYER_DIFFICULTY_MAP.put(uuid, new PlayerDataObject(DifficultyHandler.getDefaultDifficulty()));
        }
        return PLAYER_DIFFICULTY_MAP.get(uuid).difficulty;
    }

    /**
     * Sets the difficulty of the player with the given UUID to the given difficulty, updates date change info,
     * then saves player data.
     *
     * @param uuid the UUID of the player
     * @param newD the difficulty to set the player to
     */
    protected static void updateDifficulty(UUID uuid, Difficulty newD) {

        PlayerDataObject dataObject = PLAYER_DIFFICULTY_MAP.get(uuid);
        if (dataObject == null) {
            PLAYER_DIFFICULTY_MAP.put(uuid, new PlayerDataObject(newD, new Date()));
        } else {
            dataObject.difficulty = newD;
            dataObject.dateChanged = new Date();
        }

        try {
            saveDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Date getDateChanged(UUID uuid) {
        PlayerDataObject dataObject = PLAYER_DIFFICULTY_MAP.get(uuid);
        return (dataObject == null) ? null : dataObject.dateChanged;
    }

    /**
     * Creates a JSON array from the stored player data and saves it to playerdata.json.
     *
     * @throws IOException if there is a problem writing to the file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveDifficulties() throws IOException {
        Gson gson = new Gson();
        File file = new File(IDifficulty.getPlugin().getDataFolder().getAbsolutePath() + "/playerdata.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        FileWriter writer = new FileWriter(file, false);
        JsonArray playerData = new JsonArray();
        PLAYER_DIFFICULTY_MAP.forEach((k, v) -> {
            JsonObject jsonData = new JsonObject();
            jsonData.addProperty("UUID", k.toString());
            jsonData.addProperty("dateChanged", (v.dateChanged == null) ? null
                                                        : DateFormat.getDateTimeInstance().format(v.dateChanged));
            jsonData.addProperty("name", v.difficulty.getName());
            playerData.add(jsonData);
        });
        gson.toJson(playerData, writer);
        writer.flush();
        writer.close();
        IDifficulty.outputLog("iDifficulty's player data saved!");
    }

    /**
     * Reads a JSON array from playerdata.json and loads the data into memory.
     *
     * @throws IOException if there is a problem reading the file
     */
    public static void loadDifficulties() throws IOException {

        Gson gson = new Gson();
        File file = new File(IDifficulty.getPlugin().getDataFolder().getAbsolutePath() + "/playerdata.json");

        if (!file.exists()) return;

        FileReader reader = new FileReader(file);
        JsonArray playerData = gson.fromJson(reader, JsonArray.class);
        PLAYER_DIFFICULTY_MAP.clear();
        playerData.forEach(d -> {
            JsonObject jsonData = (JsonObject) d;
            String uuid = jsonData.get("UUID").getAsString();
            Difficulty difficulty = DifficultyHandler.getDifficulty(jsonData.get("name").getAsString());
            if (difficulty == null) difficulty = DifficultyHandler.getDefaultDifficulty();
            String dateChanged = jsonData.get("dateChanged").getAsString();
            PlayerDataObject dataObject;
            try {
                dataObject = new PlayerDataObject(difficulty, dateChanged);
            } catch (ParseException e) {
                IDifficulty.outputWarning("Invalid date format for " + uuid + "'s player data. Setting to null.");
                dataObject = new PlayerDataObject(difficulty);
            }
            PLAYER_DIFFICULTY_MAP.put(UUID.fromString(uuid), dataObject);

            IDifficulty.outputLog("iDifficulty's player data loaded!");
        });
    }

    /**
     * Counts the number of players with each given difficulty.
     *
     * @param includeDefault whether to include the default difficulty in this count
     * @return a map of each difficulty name to their respective counts
     */
    public static Map<String, Integer> getPlayerDifficultyCount(boolean includeDefault) {
        Map<String, Integer> dMap = new HashMap<>();
        PLAYER_DIFFICULTY_MAP.forEach((k, v) -> {
            String dName = v.difficulty.getName();
            if (includeDefault || !dName.equalsIgnoreCase(DifficultyHandler.getDefaultDifficulty().getName()))
                dMap.put(dName, dMap.getOrDefault(dName, 0) + 1);
        });
        return dMap;
    }

    private static class PlayerDataObject {

        Difficulty difficulty;
        Date dateChanged;

        PlayerDataObject(Difficulty difficulty, Date dateChanged) {
            this.difficulty = difficulty;
            this.dateChanged = dateChanged;
        }

        PlayerDataObject(Difficulty difficulty, String dateChanged) throws ParseException {
            this.difficulty = difficulty;
            this.dateChanged = DateFormat.getDateTimeInstance().parse(dateChanged);
        }

        public PlayerDataObject(Difficulty difficulty) {
            this.difficulty = difficulty;
            this.dateChanged = null;
        }
    }

}
