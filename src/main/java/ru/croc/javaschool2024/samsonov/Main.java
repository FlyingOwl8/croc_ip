package ru.croc.javaschool2024.samsonov;


import ru.croc.javaschool2024.samsonov.dao.DAO;
import ru.croc.javaschool2024.samsonov.dto.Candidate;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.*;

public class Main {

    public static void main(String[] args) {
        createTables();
        System.out.println("-");

        try {
            loadData();
        }
        catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:" + DATABASE_FILE_PATH, "samsav", "")) {
            DAO dao = new DAO(connection);
            System.out.println(dao.getRequestByPassport("passport1"));
            System.out.println(dao.getRequestByPassport("passport2"));
            System.out.println(dao.getRequestByPassport("passport3"));
            System.out.println("-");

            dao.checkRequestAndUpdateStatus(2);
            System.out.println(dao.getRequestByPassport("passport1"));
            System.out.println(dao.getRequestByPassport("passport2"));
            System.out.println(dao.getRequestByPassport("passport3"));
            System.out.println("-");

            dao.checkAllUncheckedRequestsAndUpdateStatus();
            System.out.println(dao.getRequestByPassport("passport1"));
            System.out.println(dao.getRequestByPassport("passport2"));
            System.out.println(dao.getRequestByPassport("passport3"));
            System.out.println("-");

            List<Candidate> candidateList = dao.getRegisteredCandidates();
            for (Candidate candidate: candidateList) {
                System.out.println(candidate);
            }
            System.out.println(dao.getCandidateIdByPassport("passport1"));
            System.out.println("-");

 //           System.out.println(dao.getRefusalReasonByRequestId(1));
            System.out.println(dao.getRefusalReasonByRequestId(2));
            System.out.println(dao.getRefusalReasonByRequestId(3));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при работе с БД: " + e.getMessage());
        }
    }

    private static void readCommands(DAO dao) {
        Scanner scanner = new Scanner(System.in);
        String task;
        Request request;
        PersonData personData;
        List<Request> requestList;
        List<Candidate> candidateList;
        Integer id;
        String passport;
        boolean keepRunning = true;
        while (keepRunning) {
            task = scanner.nextLine();
            switch (task) {
                case ("create request"):
                    String fullName = scanner.nextLine();
                    passport = scanner.nextLine();
                    int age = scanner.nextInt();
                    boolean foreignCitizenship = scanner.nextBoolean();
                    String educationDocument = scanner.nextLine();
                    boolean convicted = scanner.nextBoolean();
                    boolean selfNominated = scanner.nextBoolean();
                    String party = selfNominated
                            ? null
                            : scanner.nextLine();
                    Integer signs = scanner.nextInt();
                    String status = "CREATED";
                    request = new Request(null,
                            new PersonData(null, fullName, passport, age, foreignCitizenship,
                                    educationDocument, convicted),
                            selfNominated,
                            party,
                            null,
                            status
                    );
                    dao.createRequest(request);
                    break;
                case ("get request by passport"):
                    passport = scanner.nextLine();
                    request = dao.getRequestByPassport(passport);
                    System.out.println(request);
                case ("get person data by person ID"):
                    id = scanner.nextInt();
                    personData = dao.getPersonDataById(id);
                    System.out.println(personData);
                case ("delete request by request ID"):
                    id = scanner.nextInt();
                    dao.deleteRequest(id);
                case ("get all requests"):
                    requestList = dao.getAllRequests();
                    requestList.forEach(System.out::println);
                case ("check request by ID"):
                    id = scanner.nextInt();
                    dao.checkRequestAndUpdateStatus(id);
                case ("check all unchecked requests"):
                    dao.checkAllUncheckedRequestsAndUpdateStatus();
                case ("get all registered candidates"):
                    candidateList = dao.getRegisteredCandidates();
                    for (Candidate candidate: candidateList) {
                        System.out.println(candidate);
                    }
                case ("get candidate ID by passport"):
                    passport = scanner.nextLine();
                    id = dao.getCandidateIdByPassport(passport);
                    System.out.println(id);
                case ("delete candidate by candidate ID"):
                    id = scanner.nextInt();
                    dao.deleteCandidate(id);
                case ("exit"):
                    keepRunning = false;
                    break;
                default:
                    System.out.println("unknown task");
                    break;
            }
        }
    }

    private static void createTables() {
        String sqlCreatePersonDataTable = "CREATE TABLE IF NOT EXISTS " + PERSON_DATA_TABLE_NAME + "(" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "full_name VARCHAR(100), " +
                "passport VARCHAR(10), " +
                "age INTEGER, " +
                "foreign_citizenship BOOLEAN, " +
                "education VARCHAR(100), " +
                "convicted BOOLEAN" +
                ")";
        String sqlCreateRequestsTable = "CREATE TABLE IF NOT EXISTS " + REQUESTS_TABLE_NAME + "(" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "person_id INTEGER, " +
                "self_nominated BOOLEAN, " +
                "party VARCHAR(20), " +
                "signs TEXT, " +
                "status VARCHAR(10), " +
                "FOREIGN KEY (person_id) REFERENCES " + PERSON_DATA_TABLE_NAME + "(id) ON DELETE CASCADE" +
                ")";
        String sqlCreateCandidatesTable = "CREATE TABLE IF NOT EXISTS " + CANDIDATES_TABLE_NAME + "(" +
                "candidate_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "request_id INTEGER, " +
                "FOREIGN KEY (request_id) REFERENCES " + REQUESTS_TABLE_NAME + "(id) ON DELETE CASCADE" +
                ")";
        String sqlCreateRefusalsTable = "CREATE TABLE IF NOT EXISTS " + REFUSALS_TABLE_NAME + "(" +
                "request_id INTEGER, " +
                "refusal_reason TEXT, " +
                "FOREIGN KEY (request_id) REFERENCES " + REQUESTS_TABLE_NAME + "(id) ON DELETE CASCADE" +
                ")";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:" + DATABASE_FILE_PATH, "samsav", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlCreatePersonDataTable);
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlCreateRequestsTable);
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlCreateCandidatesTable);
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlCreateRefusalsTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблиц БД: " + e.getMessage());
        }
    }

    public static void loadData() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:" + DATABASE_FILE_PATH, "samsav", "")) {
            DAO dao = new DAO(connection);
            Request request1 = new Request(null,
                    new PersonData(null, "name1", "passport1", 40,
                            false, "document1", false),
                    false,
                    "КП",
                    new HashMap<>(),
                    null
                    );
            Request request2 = new Request(null,
                    new PersonData(null, "name2", "passport2", 18,
                            false, "document2", false),
                    true,
                    null,
                    new HashMap<>() {{ put("center", 500000); }},
                    null
            );
            Request request3 = new Request(null,
                    new PersonData(null, "name3", "passport3", 45,
                            false, "document3", false),
                    true,
                    null,
                    new HashMap<>() {{ put("center", 500); }},
                    null
            );
            Request request4 = new Request(null,
                    new PersonData(null, "name4", "passport4", 45,
                            false, "document4", false),
                    false,
                    "КП",
                    new HashMap<>(),
                    null
            );
            dao.createRequest(request1);
            dao.createRequest(request2);
            dao.createRequest(request3);
            //dao.createRequest(request4);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при работе с БД: " + e.getMessage());
        }
    }
}