package org.pdxfinder.validator.tablevalidation.error;

public class IllegalValueError extends ValidationErrorBuilder {

  private String errorType = "illegal Value";

  public IllegalValueError(
      String tableName, String description, String columnName, String provider) {
    super.buildValidationErrors(errorType, tableName, description, columnName);
  }

  public static String buildDescription(int count, String errorDescription, String invalidValues) {
    return String.format("found %s values %s : %s", count, errorDescription, invalidValues);
  }
}