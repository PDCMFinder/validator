package org.pdxfinder.validator.tablevalidation.enums;

public enum ErrorTypes {
  BROKEN_RELATION("Broken Relation"),
  DUPLICATE_VALUES("Duplicate Values"),
  MISSING_VALUE("Missing Value"),
  ILLEGAL_VALUE("Illegal Value"),
  MISSING_COLUMN("Missing Column"),
  MISSING_TABLE("Missing Table"),
  ;

  private String errorType;

  ErrorTypes(String errorType) {
    this.errorType = errorType;
  }

  public String getErrorType() {
    return errorType;
  }
}
