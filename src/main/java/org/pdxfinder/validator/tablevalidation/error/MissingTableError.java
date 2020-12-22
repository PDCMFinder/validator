package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.DTO.TableErrors;

public class MissingTableError implements ValidationError {
  private String tableName;
  private String provider;

  MissingTableError(String tableName, String provider) {
    this.tableName = tableName;
    this.provider = provider;
  }

  @Override
  public String message() {
    return String.format(
        "Error in [%s] for provider [%s]: Missing required table", tableName, provider);
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
