package me.kirkfox.idifficulty.difficulty;

import java.util.Date;
import java.util.UUID;

public class PlayerDifficulty extends Difficulty {

    private final UUID UUID;
    private Date dateChanged = null;

    public PlayerDifficulty(UUID uuid, Difficulty d) {
        super(d);
        this.UUID = uuid;
    }

    public UUID getUUID() {
        return UUID;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date date) {
        dateChanged = date;
    }

}
