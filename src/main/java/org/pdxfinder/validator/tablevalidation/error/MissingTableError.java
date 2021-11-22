package org.pdxfinder.validator.tablevalidation.error;

public class MissingTableError extends ValidationErrorBuilder {

  private String errorType = "missing table";
  private String description;

  public MissingTableError(String tableName) {
    this.description = buildDescription(tableName);
    super.buildValidationErrors(errorType, tableName, description, "whole table error");
  }

  static String buildDescription(String tableName) {
    return String.format("Missing required table: [%s]", tableName);
  }
}
