package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.ColumnReference;
import tech.tablesaw.api.Table;

public class EmptyValueError extends ValidationErrorBuilder {

  private Table invalidRows;
  private String errorType = "Empty value error";
  private String message;
  private String description;

  EmptyValueError(
      ColumnReference nonEmptyColumn,
      Table invalidRows,
      String provider,
      String missingRowNumbers) {
    description = buildDescription(missingRowNumbers);
    super.buildValidationErrors(
        errorType, nonEmptyColumn.table(), description, nonEmptyColumn.column());
    this.message = buildMessage(nonEmptyColumn.table(), provider, description);
    this.invalidRows = invalidRows;
  }

  private String buildDescription(String missingColumns) {
    return String.format("Missing value(s) in row numbers [%s]", missingColumns);
  }

  private String buildMessage(String table, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", table, provider, description);
  }

  @Override
  public String verboseMessage() {
    return String.format("%s:%n%s", message(), getInvalidRows());
  }

  @Override
  public String message() {
    return message;
  }

  private Table getInvalidRows() {
    return invalidRows;
  }

  @Override
  public String toString() {
    return verboseMessage();
  }
}
