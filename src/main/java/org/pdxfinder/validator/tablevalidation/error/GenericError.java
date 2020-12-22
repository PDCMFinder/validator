package org.pdxfinder.validator.tablevalidation.error;

public class GenericError implements ValidationError {
  @Override
  public String message() {
    return "Generic error";
  }
}
