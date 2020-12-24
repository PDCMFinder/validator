package org.pdxfinder.validator.tablevalidation.error;

import java.util.Set;
import org.pdxfinder.validator.tablevalidation.ColumnReference;

public class DuplicateValueError extends ValidationErrorBuilder {

  private String errorType = "value duplication";
  private String description;
  private String message;

  DuplicateValueError(ColumnReference uniqueColumn, Set<String> duplicateValues, String provider) {
    this.description = buildDescription(uniqueColumn.column(), duplicateValues.toString());
    this.message = buildMessage(uniqueColumn.table(), provider, description);
    super.buildValidationErrors(
        errorType, uniqueColumn.table(), description, duplicateValues.toString());
  }

  public String buildDescription(String columName, String duplicateValues) {
    return description =
        String.format("Duplicates found in column [%s]: %s", columName, duplicateValues);
  }

  private String buildMessage(String table, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", table, provider, description);
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
