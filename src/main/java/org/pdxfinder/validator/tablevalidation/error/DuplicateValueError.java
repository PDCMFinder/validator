package org.pdxfinder.validator.tablevalidation.error;

import java.util.Set;
import org.pdxfinder.validator.tablevalidation.ColumnReference;
import org.pdxfinder.validator.tablevalidation.DTO.TableErrors;

public class DuplicateValueError implements ValidationError {
  private ColumnReference uniqueColumn;
  private Set<String> duplicateValues;
  private String provider;

  DuplicateValueError(ColumnReference uniqueColumn, Set<String> duplicateValues, String provider) {
    this.uniqueColumn = uniqueColumn;
    this.duplicateValues = duplicateValues;
    this.provider = provider;
  }

  private Set<String> getDuplicateValues() {
    return this.duplicateValues;
  }

  @Override
  public String message() {
    return String.format(
        "Error in [%s] for provider [%s]: Duplicates found in column [%s]: %s",
        uniqueColumn.table(), provider, uniqueColumn.column(), getDuplicateValues());
  }

  @Override
  public TableErrors getTableErrors() {
    return null;
  }

  @Override
  public String toString() {
    return verboseMessage();
  }
}
