package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorTypes;

public class BrokenRelationErrorBuilder extends ValidationErrorBuilder {

  private String tableName;
  private String errorType = ErrorTypes.BROKEN_RELATION.getErrorType();
  private String description;
  private Relation relation;

  public BrokenRelationErrorBuilder(
      String tableName,
      Relation relation,
      String additionalDescription) {
    this.tableName = tableName;
    this.description = buildDescription(relation, additionalDescription);
    this.relation = relation;
  }

  public ValidationError build() {
    return buildValidationErrors(tableName, errorType, description, relation.toString());
  }

  static String buildDescription(Relation relation, String additionalDescription) {
    return String.format(
        "Broken %s relation [%s]: %s",
        relation.getValidity(), relation.toString(), additionalDescription);
  }
}