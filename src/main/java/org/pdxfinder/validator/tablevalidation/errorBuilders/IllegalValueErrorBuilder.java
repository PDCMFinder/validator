package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class IllegalValueErrorBuilder extends ValidationErrorBuilder {

    private final int count;
    private String errorType = ErrorType.ILLEGAL_VALUE.getErrorType();
    private String tableName;
    private String description;
    private String columnName;
    private String rule;
    private String cause;


    public IllegalValueErrorBuilder(String tableName, String columnName, int count) {

        this.tableName = tableName;
        this.columnName = columnName;
        this.count = count;
    }

    @Override
    public ValidationErrorBuilder<?> buildCause(String cause) {
        this.cause = String.format("found %s invalid values %s", count, cause);
        return this;
    }

    @Override
    public ValidationErrorBuilder<?> buildRule(String rule) {
        this.rule = rule;
        return this;
    }

    @Override
    public ValidationError build() {
        return new ValidationError.Builder(errorType)
                .setTableName(tableName)
                .setColumnName(columnName)
                .setRule(rule)
                .setCause(cause)
                .build();
    }
}