package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.Relation;

public class BrokenRelationError extends ValidationErrorBuilder {

  private String errorType = "Broken Relation";
  private String description;

  public BrokenRelationError(
      String tableName,
      Relation relation,
      String additionalDescription) {
    this.description = buildDescription(relation, additionalDescription);
    super.buildValidationErrors(tableName, errorType, description, relation.toString());
  }

  static String buildDescription(Relation relation, String additionalDescription) {
    return String.format(
        "Broken %s relation [%s]: %s",
        relation.getValidity(), relation.toString(), additionalDescription);
  }
}