package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class MissingColumnErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorType.MISSING_COLUMN.getErrorType();
  private String tablename;
  private String columnName;
  private String cause = "Required column not found in table";
  private String rule = "";

  public MissingColumnErrorBuilder(String tableName, String columnName) {
    this.tablename = tableName;
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

  @Override
  public ValidationError build() {
    return new ValidationError.Builder(errorType)
            .setTableName(tablename)
            .setColumnName(columnName)
            .setCause(cause)
            .setRule(rule)
            .build();
  }
}
