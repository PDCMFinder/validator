package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class MissingColumnErrorBuilder extends ValidationErrorBuilder {

  private String errorType = ErrorTypes.MISSING_COLUMN.getErrorType();
  private String tablename;
  private String description;
  private String columnName;

  public MissingColumnErrorBuilder(ColumnReference columnReference) {
    this.tablename = columnReference.table();
    this.description = buildDescription(columnReference.column());
    this.columnName = columnReference.column();
  }

  static String buildDescription(String columnName) {
    return String.format("Missing column: [%s]", columnName);
  }

  @Override
  public ValidationError build() {
    return super.buildValidationErrors(
        errorType, tablename, description, columnName);
  }
}
