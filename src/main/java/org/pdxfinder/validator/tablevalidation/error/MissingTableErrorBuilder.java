package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class MissingTableErrorBuilder extends ValidationErrorBuilder {

    private String errorType = ErrorType.MISSING_COLUMN.getErrorType();
    private String tableName;
    private String cause = "Required table is not found";
    private String rule;

    public MissingTableErrorBuilder(String tableName) {
        this.tableName = tableName;
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

    @Override
    public ValidationError build() {
        return new ValidationError.Builder(errorType)
                .setTableName(tableName)
                .setRule(rule)
                .setCause(cause)
                .build();
    }
}
