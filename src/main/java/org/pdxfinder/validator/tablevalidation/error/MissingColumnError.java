package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;

public class MissingColumnError extends ValidationErrorBuilder {

  private String errorType = "missing column";
  private String description;

  public MissingColumnError(ColumnReference columnReference) {
    this.description = buildDescription(columnReference.column());
    super.buildValidationErrors(
        errorType, columnReference.table(), description, columnReference.column());
  }

  static String buildDescription(String columnName) {
    return String.format("Missing column: [%s]", columnName);
  }
}
