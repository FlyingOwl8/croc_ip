package ru.croc.javaschool2024.samsonov;


import ru.croc.javaschool2024.samsonov.console.Console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.*;

//основной класс
public class Main {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DATABASE_FILE_PATH)) {
            Console console = new Console(connection);
            console.startWork();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при работе с БД: " + e.getMessage());
        }
    }
}