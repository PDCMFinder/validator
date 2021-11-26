package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class MissingValueErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorTypes.MISSING_VALUE.getErrorType();
  private ColumnReference nonEmptyColumn;
  private String description;

  public MissingValueErrorBuilder(
      ColumnReference nonEmptyColumn,
      String missingRowNumbers) {
    this.nonEmptyColumn = nonEmptyColumn;
    this.description = buildDescription(missingRowNumbers);
  }

  private String buildDescription(String missingColumns) {
    return String.format("Missing value(s) in row numbers: %s", missingColumns);
  }

  @Override
  public ValidationError build() {
    return super.buildValidationErrors(
        errorType, nonEmptyColumn.table(), description, nonEmptyColumn.column());
  }
}
