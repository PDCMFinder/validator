package org.pdxfinder.validator.tablevalidation.error;

public class IllegalValueError extends ValidationErrorBuilder {

  private String errorType = "illegal Value";

  public IllegalValueError(
      String tableName, String description, String columnName, String provider) {
    super.buildValidationErrors(errorType, tableName, description, columnName);
    buildMessage(tableName, provider, description);
  }

  static String buildDescription(int count, String errorDescription, String invalidValues) {
    return String.format("found %s values %s : %s", count, errorDescription, invalidValues);
  }

  private String buildMessage(String tableName, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", tableName, provider, description);
  }
}

