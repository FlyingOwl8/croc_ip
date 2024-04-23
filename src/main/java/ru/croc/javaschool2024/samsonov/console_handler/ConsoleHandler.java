package ru.croc.javaschool2024.samsonov.console_handler;

import ru.croc.javaschool2024.samsonov.dao.DAO;
import ru.croc.javaschool2024.samsonov.dto.Candidate;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;

import java.sql.Connection;
import java.util.*;


public class ConsoleHandler {
    private final Scanner scanner;
    private final Connection connection;
    private final DAO dao;

    public ConsoleHandler(Connection connection) {
        this.connection = connection;
        dao = new DAO(connection);
        scanner = new Scanner(System.in);
    }

    public void startWork() {
        try {
            dao.createTables();
        }
        catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("""
                            ---------
                            Доступные команды:
                            создать заявку
                            получить данные заявки по паспорту
                            получить личные данные по ID выдвиженца
                            удалить заявку по ID заявки
                            получить все заявки
                            проверить заявку по ID заявки
                            проверить все непроверенные заявки
                            получить данные всех зарегистрированных кандидатов
                            получить ID кандидата по паспорту
                            удалить кандидата по ID кандидата
                            получить причину отказа в регистрации по ID заявки
                            выход
                            ---------""");

        String task;
        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("Введите команду: ");
            task = scanner.nextLine();
            switch (task) {
                case ("создать заявку"):
                    createRequest();
                    break;
                case ("получить данные заявки по паспорту"):
                    getRequestByPassport();
                    break;
                case ("получить личные данные по ID выдвиженца"):
                    getPersonDataByPersonId();
                    break;
                case ("удалить заявку по ID заявки"):
                    deleteRequestByRequestId();
                    break;
                case ("получить все заявки"):
                    getAllRequests();
                    break;
                case ("проверить заявку по ID заявки"):
                    checkRequestById();
                    break;
                case ("проверить все непроверенные заявки"):
                    checkAllUncheckedRequests();
                    break;
                case ("получить данные всех зарегистрированных кандидатов"):
                    getAllRegisteredCandidates();
                    break;
                case ("получить ID кандидата по паспорту"):
                    getCandidateIdByPassport();
                    break;
                case ("удалить кандидата по ID кандидата"):
                    deleteCandidateByCandidateId();
                    break;
                case ("получить причину отказа в регистрации по ID заявки"):
                    getRefusalReasonByRequestId();
                    break;
                case ("выход"):
                    keepRunning = false;
                    break;
                default:
                    System.out.println("""
                            ---------
                            Неизвестная команда. Доступные команды:
                            создать заявку
                            получить данные заявки по паспорту
                            получить личные данные по ID выдвиженца
                            удалить заявку по ID заявки
                            получить все заявки
                            проверить заявку по ID заявки
                            проверить все непроверенные заявки
                            получить данные всех зарегистрированных кандидатов
                            получить ID кандидата по паспорту
                            удалить кандидата по ID кандидата
                            получить причину отказа в регистрации по ID заявки
                            выход
                            ---------""");
                    break;
            }
        }
    }

    private void createRequest() {
        try {
            System.out.print("ФИО: ");
            String fullName = scanner.nextLine();
            System.out.print("Паспорт: ");
            String passport = scanner.nextLine();
            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Имеется гражданство другой страны: ");
            boolean foreignCitizenship = Boolean.parseBoolean(scanner.nextLine());
            System.out.print("Документ об образовании: ");
            String educationDocument = scanner.nextLine();
            System.out.print("Имеется судимость: ");
            boolean convicted = Boolean.parseBoolean(scanner.nextLine());
            System.out.print("Самовыдвиженец: ");
            boolean selfNominated = Boolean.parseBoolean(scanner.nextLine());
            String party;
            if (selfNominated) {
                party = null;
            }
            else {
                System.out.print("Партия: ");
                party = scanner.nextLine();
            }
            System.out.print("Подписи: ");
            Map<String, Integer> signs;
            String signsString = scanner.nextLine();
            if (!Objects.equals(signsString, "none")) {
                signs = new HashMap<>();
                Arrays.stream(signsString.split(","))
                        .forEach(record -> {
                            String[] parsedRecord = record.split(":", 2);
                            String district = parsedRecord[0];
                            int districtSigns = Integer.parseInt(parsedRecord[1]);
                            signs.put(district, districtSigns);
                        });
            } else {
                signs = null;
            }
            String status = "CREATED";
            Request request = new Request(null,
                    new PersonData(null, fullName, passport, age, foreignCitizenship,
                            educationDocument, convicted),
                    selfNominated,
                    party,
                    signs,
                    status
            );
            try {
                dao.createRequest(request);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getRequestByPassport() {
        try {
            System.out.print("Паспорт: ");
            String passport = scanner.nextLine();
            try {
                Request request = dao.getRequestByPassport(passport);
                System.out.println(request);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getPersonDataByPersonId() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            try {
                PersonData personData = dao.getPersonDataById(id);
                System.out.println(personData);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteRequestByRequestId() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            try {
                dao.deleteRequest(id);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllRequests() {
        try {
            try {
                List<Request> requestList = dao.getAllRequests();
                requestList.forEach(System.out::println);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkRequestById() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            try {
                dao.checkRequestAndUpdateStatus(id);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkAllUncheckedRequests() {
        try {
            try {
                dao.checkAllUncheckedRequestsAndUpdateStatus();
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllRegisteredCandidates() {
        try {
            try {
                List<Candidate> candidateList = dao.getRegisteredCandidates();
                for (Candidate candidate: candidateList) {
                    System.out.println(candidate);
                }
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getCandidateIdByPassport() {
        try {
            try {
                System.out.print("Паспорт: ");
                String passport = scanner.nextLine();
                int id = dao.getCandidateIdByPassport(passport);
                System.out.println(id);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteCandidateByCandidateId() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            try {
                dao.deleteCandidate(id);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getRefusalReasonByRequestId() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            try {
                dao.getRefusalReasonByRequestId(id);
                System.out.println("---SUCCESS---");
            }
            catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
