package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.Relation;
import tech.tablesaw.api.Table;

public class BrokenRelationError extends ValidationErrorBuilder {

  private String errorType = "Broken Relation";
  private String description;
  private Table invalidRows;

  public BrokenRelationError(
      String tableName,
      Relation relation,
      Table invalidRows,
      String additionalDescription) {
    this.description = buildDescription(relation, additionalDescription);
    this.invalidRows = invalidRows;
    super.buildValidationErrors(tableName, errorType, description, relation.toString());
  }

  static String buildDescription(Relation relation, String additionalDescription) {
    return String.format(
        "Broken %s relation [%s]: %s",
        relation.getValidity(), relation.toString(), additionalDescription);
  }

  private Table getInvalidRows() {
    return this.invalidRows;
  }

}
