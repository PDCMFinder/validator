package org.pdxfinder.validator.tablevalidation;

import com.google.gson.Gson;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.pdxfinder.validator.tablevalidation.DTO.ErrorReport;
import org.pdxfinder.validator.tablevalidation.DTO.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorReporter {

  private List<ValidationError> errors;
  private ErrorReport errorReport;
  private Gson gson;
  private static final Logger log = LoggerFactory.getLogger(ErrorReporter.class);

  public ErrorReporter(List<ValidationError> errors) {
    this.errors = errors;
  }

  public String getJson() {
    gson = new Gson();
    errorReport = new ErrorReport();
    errorReport.setValidationErrors(errors);
    return gson.toJson(errorReport);
  }

  public int count() {
    return errors.size();
  }

  public void logErrors() {
    if (CollectionUtils.isNotEmpty(errors)) {
      log.error("{} validation errors found:", errors.size());
      for (ValidationError error : errors) {
        String message = error.getTableReport().getColumnReport().getMessage();
        log.error(message);
      }
    } else {
      log.info("There were no validation errors raised, great!");
    }
  }

  public ErrorReporter truncate(int limit) {
    log.info("Limiting output to the first {} errors:", limit);
    return new ErrorReporter(truncateList(errors, limit));
  }

  private <E> List<E> truncateList(List<E> list, int size) {
    if (list.size() > size) {
      return list.subList(0, size);
    } else {
      return list;
    }
  }
}
