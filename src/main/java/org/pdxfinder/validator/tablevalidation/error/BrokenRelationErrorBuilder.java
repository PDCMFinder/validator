package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;

public class BrokenRelationErrorBuilder extends ValidationErrorBuilder {

  private String tableName;
  private String errorType = ErrorType.BROKEN_RELATION.getErrorType();
  private String columnName;
  private String cause;
  private String rule;


  public BrokenRelationErrorBuilder(
          String tableName,
          String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  @Override
  public ValidationErrorBuilder<?> buildCause(String causeDescription) {
    this.cause = causeDescription;
    return this;
  }

  @Override
  public ValidationErrorBuilder<?> buildRule(String validityType) {
    this.rule = String.format("Broken %s relation", validityType);
    return this;
  }

  public ValidationError build() {
    return new ValidationError.Builder(errorType)
            .setTableName(tableName)
            .setColumnName(columnName)
            .setRule(rule)
            .setCause(cause)
            .build();
  }
}