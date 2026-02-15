package ru.sinvic.exception;

public class SessionNotFoundException extends EntityNotFoundException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
