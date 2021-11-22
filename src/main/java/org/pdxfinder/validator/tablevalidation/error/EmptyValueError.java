package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import tech.tablesaw.api.Table;

public class EmptyValueError extends ValidationErrorBuilder {

  private String errorType = "Empty value error";
  private String description;

  EmptyValueError(
      ColumnReference nonEmptyColumn,
      Table invalidRows,
      String provider,
      String missingRowNumbers) {
    description = buildDescription(missingRowNumbers);
    super.buildValidationErrors(
        errorType, nonEmptyColumn.table(), description, nonEmptyColumn.column());
    buildMessage(nonEmptyColumn.table(), provider, description);
  }

  private String buildDescription(String missingColumns) {
    return String.format("Missing value(s) in row numbers [%s]", missingColumns);
  }

  private String buildMessage(String table, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", table, provider, description);
  }
}
