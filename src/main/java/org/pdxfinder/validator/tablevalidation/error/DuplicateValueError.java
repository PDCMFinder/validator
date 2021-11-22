package org.pdxfinder.validator.tablevalidation.error;

import java.util.Set;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;

public class DuplicateValueError extends ValidationErrorBuilder {

  private String errorType = "value duplication";
  private String description;

  DuplicateValueError(ColumnReference uniqueColumn, Set<String> duplicateValues, String provider) {
    this.description = buildDescription(duplicateValues.toString());
    buildMessage(uniqueColumn.table(), provider, description);
    super.buildValidationErrors(
        errorType, uniqueColumn.table(), description, uniqueColumn.column());
  }

  public String buildDescription(String duplicateValues) {
    return String.format("Duplicates found : %s", duplicateValues);
  }

  private String buildMessage(String table, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", table, provider, description);
  }
}
