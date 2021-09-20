package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.Relation;
import tech.tablesaw.api.Table;

public class BrokenRelationError extends ValidationErrorBuilder {

  private String errorType = "Broken Relation";
  private String message;
  private String description;
  private Table invalidRows;

  public BrokenRelationError(
      String tableName,
      Relation relation,
      Table invalidRows,
      String additionalDescription,
      String provider) {
    this.description = buildDescription(relation, additionalDescription);
    this.message = buildMessage(tableName, provider, description);
    this.invalidRows = invalidRows;
    super.buildValidationErrors(tableName, errorType, description, relation.toString());
  }

  static String buildDescription(Relation relation, String additionalDescription) {
    return String.format(
        "Broken %s relation [%s]: %s",
        relation.getValidity(), relation.toString(), additionalDescription);
  }

  private static String buildMessage(String tableName, String provider, String description) {
    return String.format("Error in [%s] for provider [%s]: %s", tableName, provider, description);
  }

  private Table getInvalidRows() {
    return this.invalidRows;
  }

  public String message() {
    return message;
  }

  public String toString() {
    return verboseMessage();
  }

  public String verboseMessage() {
    return String.format("%s:%n%s", message(), getInvalidRows());
  }
}
