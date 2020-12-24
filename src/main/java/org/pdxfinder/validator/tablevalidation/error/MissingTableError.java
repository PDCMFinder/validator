package org.pdxfinder.validator.tablevalidation.error;

public class MissingTableError extends ValidationErrorBuilder {

  private String errorType = "missing table";
  private String description;
  private String message;

  MissingTableError(String tableName, String provider) {
    this.description = buildDescription(tableName);
    this.message = buildMessage(tableName, provider, description);
    super.buildValidationErrors(errorType, tableName, description, "whole table error");
  }

  static String buildDescription(String tableName) {
    return String.format("Missing required table: [%s]", tableName);
  }

  static String buildMessage(String tableName, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", tableName, provider, description);
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  String verboseMessage() {
    return message;
  }

  @Override
  public String toString() {
    return verboseMessage();
  }
}
