package ru.croc.javaschool2024.samsonov.service;

import lombok.RequiredArgsConstructor;
import ru.croc.javaschool2024.samsonov.dao.DAO;
import ru.croc.javaschool2024.samsonov.dto.Candidate;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;
import ru.croc.javaschool2024.samsonov.exception.DAOException;
import ru.croc.javaschool2024.samsonov.exception.ServiceException;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

//класс сервиса
@RequiredArgsConstructor
public class Service {
    private final DAO dao;

    public Service(Connection connection) {
        dao = new DAO(connection);
    }

    //создать таблицы
    public void createTables() {
        try {
            dao.createTables();
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //создать заявку
    public void createRequest(Request request) {
        validateRequest(request);
        try {
            dao.createRequest(request);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить данные заявки по паспорту
    public Request getRequestByPassport(String passport) {
        if (passport.isBlank()) {
            throw new ServiceException("Паспортные данные не могут быть пустой строкой");
        }
        try {
            return dao.getRequestByPassport(passport);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить личные данные по ID выдвиженца
    public PersonData getPersonDataById(int id) {
        if (id <= 0) {
            throw new ServiceException("ID должен быть положительным числом");
        }
        try {
            return dao.getPersonDataById(id);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //удалить заявку по ID заявки
    public void deleteRequest(int id) {
        if (id <= 0) {
            throw new ServiceException("ID должен быть положительным числом");
        }
        try {
            dao.deleteRequest(id);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить все заявки
    public List<Request> getAllRequests() {
        try {
            return dao.getAllRequests();
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //проверить заявку по ID заявки
    public void checkRequestAndUpdateStatus(int id) {
        if (id <= 0) {
            throw new ServiceException("ID должен быть положительным числом");
        }
        try {
            dao.checkRequestAndUpdateStatus(id);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //проверить все непроверенные заявки
    public void checkAllUncheckedRequestsAndUpdateStatus() {
        try {
            dao.checkAllUncheckedRequestsAndUpdateStatus();
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить данные всех зарегистрированных кандидатов
    public List<Candidate> getRegisteredCandidates() {
        try {
            return dao.getRegisteredCandidates();
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить ID кандидата по паспорту
    public int getCandidateIdByPassport(String passport) {
        if (passport.isBlank()) {
            throw new ServiceException("Паспортные данные не могут быть пустой строкой");
        }
        try {
            return dao.getCandidateIdByPassport(passport);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //удалить кандидата по ID кандидата
    public void deleteCandidate(int id) {
        if (id <= 0) {
            throw new ServiceException("ID должен быть положительным числом");
        }
        try {
            dao.deleteCandidate(id);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //получить причину отказа в регистрации по ID заявки
    public String getRefusalReasonByRequestId(int id) {
        if (id <= 0) {
            throw new ServiceException("ID должен быть положительным числом");
        }
        try {
            return dao.getRefusalReasonByRequestId(id);
        }
        catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    //проверить валидность данных заявки
    private void validateRequest(Request request) {
        validatePersonData(request.getPersonData());
        if (!request.isSelfNominated() && (request.getParty() == null || request.getParty().isBlank())) {
            throw new ServiceException("Не указана партия выдвиженца");
        }
        if (request.getSigns().containsKey("")) {
            throw new ServiceException("Район не может быть пустой строкой");
        }
        if (request.getSigns().values().stream().anyMatch(districtSigns -> districtSigns<0)) {
            throw new ServiceException("Количество подписей должно быть положительным числом");
        }
        if (!(Objects.equals(request.getStatus(), "CREATED") ||
                Objects.equals(request.getStatus(), "REFUSED") ||
                Objects.equals(request.getStatus(), "ACCEPTED"))
        ) {
            throw new ServiceException("Неизвестный статус заявки");
        }
    }

    //проверить валидность личных данных
    private void validatePersonData(PersonData personData) {
        if (personData.getFullName().isBlank()) {
            throw new ServiceException("ФИО не может быть пустой строкой");
        }
        if (personData.getPassport().isBlank()) {
            throw new ServiceException("Паспортные данные не могут быть пустой строкой");
        }
        if (personData.getAge() <= 0) {
            throw new ServiceException("Возраст дожен быть положительным числом");
        }
        if (personData.getEducationDocument().isBlank()) {
            throw new ServiceException("Данные об образовании не могут быть пустой строкой");
        }
    }
}
