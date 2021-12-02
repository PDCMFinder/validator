package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class MissingValueErrorBuilder extends ValidationErrorBuilder {

    private String errorType = ErrorType.MISSING_VALUE.getErrorType();
    private String tableName;
    private String columnName;
    private String rule = "Required value";
    private String cause = "No value found";
    private String rows;

    public MissingValueErrorBuilder(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public ValidationErrorBuilder<?> buildCause(String cause) {
        this.cause = cause;
        return this;
    }

    @Override
    public ValidationErrorBuilder<?> buildRule(String rule) {
        this.rule = rule;
        return this;
    }

    public MissingValueErrorBuilder buildRows(String rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public ValidationError build() {
        return new ValidationError.Builder(errorType)
                .setTableName(tableName)
                .setColumnName(columnName)
                .setRule(rule)
                .setCause(cause)
                .setRow(rows)
                .build();
    }
}
