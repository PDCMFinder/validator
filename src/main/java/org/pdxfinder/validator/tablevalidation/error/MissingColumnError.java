package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.ColumnReference;
import org.pdxfinder.validator.tablevalidation.DTO.TableErrors;

public class MissingColumnError implements ValidationError {
  private ColumnReference columnReference;
  private String provider;

  MissingColumnError(ColumnReference columnReference, String provider) {
    this.columnReference = columnReference;
    this.provider = provider;
  }

  @Override
  public String message() {
    return String.format(
        "Error in [%s] for provider [%s]: Missing column: [%s]",
        columnReference.table(), provider, columnReference.column());
  }

  @Override
  public TableErrors getTableErrors() {
    return null;
  }

  @Override
  public String toString() {
    return message();
  }
}
