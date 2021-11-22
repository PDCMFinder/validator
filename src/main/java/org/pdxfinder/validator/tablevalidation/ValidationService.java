package org.pdxfinder.validator.tablevalidation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.pdxfinder.validator.tablevalidation.dto.ErrorReport;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error_creators.BrokenRelationErrorCreator;
import org.pdxfinder.validator.tablevalidation.error_creators.DuplicateValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error_creators.EmptyValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error_creators.IllegalValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error_creators.MissingColumnErrorCreator;
import org.pdxfinder.validator.tablevalidation.error_creators.MissingTableErrorCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.Table;

@Service
public class ValidationService {

  private static final Logger log = LoggerFactory.getLogger(ValidationService.class);
  private List<ValidationError> validationErrors;

  public List<ValidationError> validate(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors = new ArrayList<>();
    checkRequiredTablesPresent(tableSet, tableSetSpecification);
    if (thereAreNoErrors(validationErrors, tableSetSpecification)) {
      checkRequiredColumnsPresent(tableSet, tableSetSpecification);
      if (thereAreNoErrors(validationErrors, tableSetSpecification)) {
        performColumnValidation(tableSet, tableSetSpecification);
      }
    }
    return validationErrors;
  }

  private void performColumnValidation(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    checkAllNonEmptyValuesPresent(tableSet, tableSetSpecification);
    checkForIllegalValues(tableSet, tableSetSpecification);
    checkAllUniqueColumnsForDuplicates(tableSet, tableSetSpecification);
    checkRelationsValid(tableSet, tableSetSpecification);
  }

  private boolean thereAreNoErrors(
      List<ValidationError> validationErrors, TableSetSpecification tableSetSpecification) {
    if (CollectionUtils.isNotEmpty(validationErrors)) {
      log.error(
          "Not all required tables and columns where present for {}. Aborting further validation. Please Fix an rerun validation",
          tableSetSpecification.getProvider());

    }
    return !CollectionUtils.isNotEmpty(validationErrors);
  }

  private void checkRequiredTablesPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    if (tableSetSpecification.hasRequiredColumns()) {
      validationErrors.addAll(
          new MissingTableErrorCreator().generateErrors(tableSet, tableSetSpecification));
    }
  }

  private void checkRequiredColumnsPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    if (tableSetSpecification.hasRequiredColumns())
      validationErrors.addAll(
          new MissingColumnErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkAllNonEmptyValuesPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new EmptyValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkAllUniqueColumnsForDuplicates(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new DuplicateValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkRelationsValid(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new BrokenRelationErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkForIllegalValues(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new IllegalValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  List<ValidationError> getValidationErrors() {
    return this.validationErrors;
  }

  public String getJsonReport(String reportName) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ErrorReport errorReport = new ErrorReport();
    if (!reportName.isBlank()) {
      errorReport.setId(reportName);
    }
    errorReport.setValidationErrors(validationErrors);
    return gson.toJson(errorReport);
  }

  public String getJsonReport() {
    return getJsonReport("");
  }
}
