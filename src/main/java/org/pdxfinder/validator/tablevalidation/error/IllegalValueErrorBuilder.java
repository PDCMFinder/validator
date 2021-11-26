package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class IllegalValueErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorTypes.ILLEGAL_VALUE.getErrorType();
  private String tableName;
  private String description;
  private String columnName;


  public IllegalValueErrorBuilder(String tableName, int count, String errorDescription,
      String invalidValue, String columnName) {
    this.description = buildDescription(count, errorDescription, invalidValue);
    this.tableName = tableName;
    this.columnName = columnName;
  }

  public static String buildDescription(int count, String errorDescription, String invalidValues) {
    return String.format("found %s values %s : %s", count, errorDescription, invalidValues);
  }

  @Override
  public ValidationError build() {
    return super.buildValidationErrors(errorType, tableName, description, columnName);
  }
}