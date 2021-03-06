package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.exception.FileReadException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 24/5/2015.
 */
class LoadingPerson extends GameObject {

    private String surname;
    private String givenName;
    private String calledName;

    private Person.State state;

    private LoadingLocationType loadingLocationType = LoadingLocationType.NONE;
    private int locationId = -1;

    private int movingDays = 0;

    private LoadingPerson(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return surname + givenName;
    }

    public static final GameObjectList<LoadingPerson> fromCSV(FileHandle root, int version) {
        GameObjectList<LoadingPerson> result = new GameObjectList<>();

        FileHandle f = root.child(Person.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                LoadingPerson data = new LoadingPerson(Integer.parseInt(line[0]));
                if (version == 1) {
                    data.surname = line[3];
                    data.givenName = line[4];
                    data.calledName = line[5];
                    if (Boolean.parseBoolean(line[1])) {
                        if (Boolean.parseBoolean(line[2])) {
                            data.state = Person.State.NORMAL;
                        } else {
                            data.state = Person.State.DEAD;
                        }
                    } else {
                        data.state = Person.State.UNDEBUTTED;
                    }
                    data.movingDays = Integer.parseInt(line[62]);
                } else {
                    data.surname = line[1];
                    data.givenName = line[2];
                    data.calledName = line[3];
                    data.state = Person.State.fromCSV(line[4]);
                    data.loadingLocationType = LoadingLocationType.fromCSV(line[5]);
                    data.locationId = Integer.parseInt(line[6]);
                    data.movingDays = Integer.parseInt(line[7]);
                }

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void setup(GameObjectList<LoadingPerson> persons, GameObjectList<LoadingArchitecture> architectures) {
        for (LoadingPerson p : persons) {
            for (LoadingArchitecture a : architectures) {
                if (a.getPersons().contains(p.getId()) || a.getMovingPersons().contains(p.getId())) {
                    p.loadingLocationType = LoadingLocationType.ARHITECTURE;
                    p.locationId = a.getId();
                    p.state = Person.State.NORMAL;
                } else if (a.getUnhiredPersons().contains(p.getId()) || a.getUnhiredMovingPersons().contains(p.getId())) {
                    p.loadingLocationType = LoadingLocationType.ARHITECTURE;
                    p.locationId = a.getId();
                    p.state = Person.State.UNHIRED;
                }
            }
        }
    }

    public Person.State getState() {
        return state;
    }

    public int getMovingDays() {
        return movingDays;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getCalledName() {
        return calledName;
    }

    public LoadingLocationType getLoadingLocationType() {
        return loadingLocationType;
    }

    public int getLocationId() {
        return locationId;
    }

    public enum LoadingLocationType {
        NONE, ARHITECTURE;

        public static LoadingLocationType fromCSV(String s) {
            switch (Integer.parseInt(s)) {
                case -1: return NONE;
                case 1: return ARHITECTURE;
            }
            assert false;
            return null;
        }

        public String toCSV() {
            switch (this) {
                case NONE: return "-1";
                case ARHITECTURE: return "1";
            }
            assert false;
            return null;
        }
    }
}
