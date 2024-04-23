package ru.croc.javaschool2024.samsonov.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(RuntimeException e) {
        super(e);
    }

    public ServiceException(String message) {
        super(message);
    }
}
