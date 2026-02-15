package ru.sinvic.exception;

public class ContentNotFoundException extends EntityNotFoundException {
    public ContentNotFoundException(String message) {
        super(message);
    }
}
