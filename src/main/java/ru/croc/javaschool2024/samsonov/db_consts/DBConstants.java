package ru.croc.javaschool2024.samsonov.db_consts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DBConstants {
    public static final String SELECTED_PARTIES_FILE_PATH = "src/main/resources/selected_parties/selected_parties.txt";
    public static final String DATABASE_PROPERTIES_FILE_PATH = "src/main/resources/db_properties/DBProperties.txt";
    public static final String DATABASE_FILE_PATH = readDBFilePath();
    public static final String PERSON_DATA_TABLE_NAME = "Person_data";
    public static final String REQUESTS_TABLE_NAME = "Requests";
    public static final String CANDIDATES_TABLE_NAME = "Candidates";
    public static final String REFUSALS_TABLE_NAME = "Refusals";

    private DBConstants() {
    }

    private static String readDBFilePath() {
        try {
            return Files.readString(Path.of(DATABASE_PROPERTIES_FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
