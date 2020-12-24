package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.DTO.ColumnReport;
import org.pdxfinder.validator.tablevalidation.DTO.TableReport;
import org.pdxfinder.validator.tablevalidation.DTO.ValidationError;

public abstract class ValidationErrorBuilder {

  private ValidationError validationError;

  public abstract String message();

  abstract String verboseMessage();

  void buildValidationErrors(
      String type, String tableName, String description, String columnDescription) {
    var validationError = new ValidationError();
    var tableReport = new TableReport();
    var columnReport = new ColumnReport();
    columnReport.setMessage(description);
    columnReport.setColumnName(columnDescription);
    tableReport.setColumnReport(columnReport);
    validationError.setTableReport(tableReport);
    validationError.setType(type);
    validationError.setTableName(tableName);
    this.validationError = validationError;
  }

  public ValidationError getValidationError() {
    return validationError;
  }
}
