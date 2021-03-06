package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 14/5/2015.
 */
public class GameData {

    public static final String SAVE_FILE = "GameData.csv";

    private @Nullable Faction currentPlayer;
    private int dayPassed;

    private GameData(){}

    public static final GameData fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        FileHandle f = root.child(SAVE_FILE);

        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                GameData data = new GameData();

                if (line[0].length() > 0) {
                    data.currentPlayer = scen.getFactions().get(Integer.parseInt(line[0]));
                } else {
                    data.currentPlayer = null;
                }
                if (version == 1) {
                    data.dayPassed = Integer.parseInt(line[5]);
                } else {
                    data.dayPassed = Integer.parseInt(line[1]);
                }

                return data;
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        throw new FileReadException(f.path(), new EmptyFileException());
    }

    public static final void toCSV(FileHandle root, GameData data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.GAME_DATA_SAVE_HEADER).split(","));
            writer.writeNext(new String[]{
                    String.valueOf(data.currentPlayer != null ? data.currentPlayer.getId() : -1),
                    String.valueOf(data.dayPassed)
            });
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public void setCurrentPlayer(@Nullable Faction currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public @Nullable Faction getCurrentPlayer() {
        return currentPlayer;
    }

    public int getDayPassed() {
        return dayPassed;
    }

    public void advanceDay() {
        dayPassed++;
    }
}
