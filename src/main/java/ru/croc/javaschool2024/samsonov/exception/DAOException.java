package ru.croc.javaschool2024.samsonov.exception;

public class DAOException extends RuntimeException {
    public DAOException(String message) {
        super(message);
    }

    public DAOException(RuntimeException e) {
        super(e);
    }
}