package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;

public class MissingColumnError extends ValidationErrorBuilder {

  private String errorType = "missing column";
  private String description;
  private String message;

  MissingColumnError(ColumnReference columnReference, String provider) {
    this.description = buildDescription(columnReference.column());
    this.message = buildMessage(columnReference.table(), provider, description);
    super.buildValidationErrors(
        errorType, columnReference.table(), description, columnReference.column());
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  String verboseMessage() {
    return message;
  }

  static String buildDescription(String columnName) {
    return String.format("Missing column: [%s]", columnName);
  }

  static String buildMessage(String tableName, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", tableName, provider, description);
  }

  @Override
  public String toString() {
    return message();
  }
}
