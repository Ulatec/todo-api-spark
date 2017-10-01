package com.teamtreehouse.techdegrees.exception;

public class TodoException extends Exception{
    private final Exception originalException;
    public TodoException(Exception originalException, String message){
        super(message);
        this.originalException = originalException;
    }
}
