package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class MissingTableErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorTypes.MISSING_COLUMN.getErrorType();
  private String tableName;
  private String description;

  public MissingTableErrorBuilder(String tableName) {
    this.tableName = tableName;
    this.description = buildDescription(tableName);
  }

  static String buildDescription(String tableName) {
    return String.format("Missing required table: [%s]", tableName);
  }

  @Override
  public ValidationError build() {
    return super.buildValidationErrors(errorType, tableName, description, "whole table error");
  }
}
