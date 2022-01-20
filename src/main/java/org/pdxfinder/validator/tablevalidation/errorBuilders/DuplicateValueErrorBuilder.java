package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class DuplicateValueErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorType.DUPLICATE_VALUES.getErrorType();
  private String tableName;
  private String columnName;
  private String cause = "";
  private String rule = "";

  public DuplicateValueErrorBuilder(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  @Override
  public ValidationErrorBuilder<?> buildCause(String duplicates) {
    this.cause = String.format("Duplicated values found: %s", duplicates);
    return this;
  }

  @Override
  public ValidationErrorBuilder<?> buildRule(String rule) {
    this.rule = rule;
    return this;
  }

  public ValidationError build() {
    return new ValidationError.Builder(errorType)
            .setTableName(tableName)
            .setColumnName(columnName)
            .setCause(cause)
            .setRule(rule)
            .build();

  }
}
