package com.bharath.jobtracker;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) { super("Application " + id + " was not found."); }
}
