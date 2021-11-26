package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dto.ColumnReport;
import org.pdxfinder.validator.tablevalidation.dto.TableReport;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;

public abstract class ValidationErrorBuilder {

  public ValidationError buildValidationErrors(
      String type, String tableName, String description, String columnDescription) {
    var error = new ValidationError();
    var tableReport = new TableReport();
    var columnReport = new ColumnReport();
    columnReport.setMessage(description);
    columnReport.setColumnName(columnDescription);
    tableReport.setColumnReport(columnReport);
    error.setTableReport(tableReport);
    error.setType(type);
    error.setTableName(tableName);
    return error;
  }

  public abstract ValidationError build();
}
