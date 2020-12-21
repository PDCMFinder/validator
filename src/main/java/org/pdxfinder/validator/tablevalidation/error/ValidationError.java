package org.pdxfinder.validator.tablevalidation.error;

public interface ValidationError {
    String message();
    default String verboseMessage() {
        return message();
    }

}
