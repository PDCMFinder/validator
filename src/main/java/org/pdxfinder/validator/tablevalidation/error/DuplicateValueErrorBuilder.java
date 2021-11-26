package org.pdxfinder.validator.tablevalidation.error;

import java.util.Set;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class DuplicateValueErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorTypes.DUPLICATE_VALUES.getErrorType();
  private ColumnReference uniqueColumn;
  private String description;


  public DuplicateValueErrorBuilder(ColumnReference uniqueColumn, Set<String> duplicateValues) {
    this.uniqueColumn = uniqueColumn;
    this.description = buildDescription(duplicateValues.toString());
  }

  public String buildDescription(String duplicateValues) {
    return String.format("Duplicates values found: %s", duplicateValues);
  }

  public ValidationError build() {
    return super.buildValidationErrors(errorType, uniqueColumn.table(), description,
        uniqueColumn.column());
  }
}
