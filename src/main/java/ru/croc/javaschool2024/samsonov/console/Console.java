package ru.croc.javaschool2024.samsonov.console;

import ru.croc.javaschool2024.samsonov.dto.Candidate;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;
import ru.croc.javaschool2024.samsonov.exception.ServiceException;
import ru.croc.javaschool2024.samsonov.service.Service;

import java.sql.Connection;
import java.util.*;


public class Console {
    private final Scanner scanner;
    private final Service service;

    public Console(Connection connection) {
        service = new Service(connection);
        scanner = new Scanner(System.in);
    }

    public void startWork() {
        try {
            service.createTables();
        }
        catch (ServiceException e) {
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
        String fullName;
        String passport;
        int age;
        boolean foreignCitizenship;
        String educationDocument;
        boolean convicted;
        boolean selfNominated;
        String party;
        Map<String, Integer> signs;
        String status;
        try {
            System.out.print("ФИО: ");
            fullName = scanner.nextLine();
            System.out.print("Паспорт: ");
            passport = scanner.nextLine();
            System.out.print("Возраст: ");
            age = Integer.parseInt(scanner.nextLine());
            System.out.print("Имеется гражданство другой страны: ");
            foreignCitizenship = Boolean.parseBoolean(scanner.nextLine());
            System.out.print("Документ об образовании: ");
            educationDocument = scanner.nextLine();
            System.out.print("Имеется судимость: ");
            convicted = Boolean.parseBoolean(scanner.nextLine());
            System.out.print("Самовыдвиженец: ");
            selfNominated = Boolean.parseBoolean(scanner.nextLine());
            if (selfNominated) {
                party = null;
            } else {
                System.out.print("Партия: ");
                party = scanner.nextLine();
            }
            System.out.print("Подписи: ");
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
            status = "CREATED";
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        Request request = new Request(null,
                new PersonData(null, fullName, passport, age, foreignCitizenship,
                        educationDocument, convicted),
                selfNominated,
                party,
                signs,
                status
        );
        try {
            service.createRequest(request);
            System.out.println("---SUCCESS---");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getRequestByPassport() {
        String passport;
        try {
            System.out.print("Паспорт: ");
            passport = scanner.nextLine();
        }
        catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }

        try {
            Request request = service.getRequestByPassport(passport);
            System.out.println(request);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getPersonDataByPersonId() {
        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            PersonData personData = service.getPersonDataById(id);
            System.out.println(personData);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteRequestByRequestId() {
        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            service.deleteRequest(id);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllRequests() {
        try {
            List<Request> requestList = service.getAllRequests();
            requestList.forEach(System.out::println);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkRequestById() {
        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            service.checkRequestAndUpdateStatus(id);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkAllUncheckedRequests() {
        try {
            service.checkAllUncheckedRequestsAndUpdateStatus();
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllRegisteredCandidates() {
        try {
            List<Candidate> candidateList = service.getRegisteredCandidates();
            for (Candidate candidate: candidateList) {
                System.out.println(candidate);
            }
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getCandidateIdByPassport() {
        String passport;
        try {
            System.out.print("Паспорт: ");
            passport = scanner.nextLine();
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            int id = service.getCandidateIdByPassport(passport);
            System.out.println(id);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteCandidateByCandidateId() {
        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            service.deleteCandidate(id);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getRefusalReasonByRequestId() {
        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (RuntimeException e) {
            System.out.println("Некорректный ввод: " + e.getMessage());
            return;
        }
        try {
            String refusalReason = service.getRefusalReasonByRequestId(id);
            System.out.println(refusalReason);
            System.out.println("---SUCCESS---");
        }
        catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }
}
