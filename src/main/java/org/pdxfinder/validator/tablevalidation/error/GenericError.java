package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.DTO.TableErrors;

public class GenericError implements ValidationError {
  @Override
  public String message() {
    return "Generic error";
  }

  @Override
  public TableErrors getTableErrors() {
    return null;
  }
}
