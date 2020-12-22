package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.DTO.TableErrors;

public interface ValidationError {

  String message();
  TableErrors getTableErrors();

  default String verboseMessage() {
    return message();
  }
}
