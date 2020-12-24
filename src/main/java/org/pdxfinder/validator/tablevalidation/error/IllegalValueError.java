package org.pdxfinder.validator.tablevalidation.error;

import tech.tablesaw.api.Table;

public class IllegalValueError extends ValidationErrorBuilder {

  private String errorType = "illegal Value";
  private String message;
  private Table invalidRows;

  public IllegalValueError(
      String tableName, String description, String columnName, Table invalidRows, String provider) {
    super.buildValidationErrors(errorType, tableName, description, columnName);
    this.message = buildMessage(tableName, provider, description);
    this.invalidRows = invalidRows;
  }

  static String buildDescription(
      String column, int count, String errorDescription, String invalidValues) {
    return String.format(
        "in column [%s] found %s values %s : %s", column, count, errorDescription, invalidValues);
  }

  private String buildMessage(String tableName, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", tableName, provider, description);
  }

  private Table getInvalidRows() {
    return this.invalidRows;
  }

  @Override
  public String verboseMessage() {
    return String.format("%s:%n%s", message(), getInvalidRows());
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  public String toString() {
    return verboseMessage();
  }
}
