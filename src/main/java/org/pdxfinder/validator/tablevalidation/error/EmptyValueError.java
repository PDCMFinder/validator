package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;

public class EmptyValueError extends ValidationErrorBuilder {

  private String errorType = "Empty value error";
  private String description;

  public EmptyValueError(
      ColumnReference nonEmptyColumn,
      String missingRowNumbers) {
    description = buildDescription(missingRowNumbers);
    super.buildValidationErrors(
        errorType, nonEmptyColumn.table(), description, nonEmptyColumn.column());
  }

  private String buildDescription(String missingColumns) {
    return String.format("Missing value(s) in row numbers [%s]", missingColumns);
  }
}
