package ru.croc.javaschool2024.samsonov;


import ru.croc.javaschool2024.samsonov.console.Console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.*;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:" + DATABASE_FILE_PATH, "samsav", "")) {
            Console console = new Console(connection);
            console.startWork();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при работе с БД: " + e.getMessage());
        }
    }
}