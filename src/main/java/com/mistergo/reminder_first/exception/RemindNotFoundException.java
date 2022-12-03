package com.mistergo.reminder_first.exception;

public class RemindNotFoundException extends RuntimeException {
    public RemindNotFoundException(String message) {
        super(message);
    }
}
