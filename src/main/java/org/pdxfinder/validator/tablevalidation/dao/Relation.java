package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Relation {


  private RelationType validity;
  private String leftTableName;
  private String leftColumnName;
  private String rightTableName;
  private String rightColumnName;

  @JsonCreator
  public Relation(
      @JsonProperty("type") String relationType,
      @JsonProperty("right_table") String rightTableName,
      @JsonProperty("right_column") String rightColumnName
  ) {
    this.validity = RelationType.parseRelationType(relationType);
    this.rightTableName = rightTableName;
    this.rightColumnName = rightColumnName;
  }

  public Relation addLeftTableAndColumn(String leftTableName, String leftColumnName) {
    this.leftTableName = leftTableName;
    this.leftColumnName = leftColumnName;
    return this;
  }

  public ColumnReference getOtherColumn(ColumnReference queriedColumn) {
    ColumnReference otherColumn;
    if (queriedColumn.equals(this.leftColumnReference())) {
      otherColumn = this.rightColumnReference();
    } else if (queriedColumn.equals(this.rightColumnReference())) {
      otherColumn = this.leftColumnReference();
    } else {
      otherColumn =
          ColumnReference.of(
              String.format("table linked to %s not found", queriedColumn.table()),
              String.format("column linked to %s not found", queriedColumn.column()));
    }
    return otherColumn;
  }

  public String leftTable() {
    return this.leftTableName;
  }

  public String leftColumn() {
    return this.leftColumnName;
  }

  public String rightTable() {
    return this.rightTableName;
  }

  public String rightColumn() {
    return this.rightColumnName;
  }

  public ColumnReference leftColumnReference() {
    return ColumnReference.of(leftTable(), leftColumn());
  }

  public ColumnReference rightColumnReference() {
    return ColumnReference.of(rightTable(), rightColumn());
  }

  public RelationType getValidity() {
    return validity;
  }

  public void setValidity(RelationType validity) {
    this.validity = validity;
  }

  public void setLeftTable(String leftTableName) {
    this.leftTableName = leftTableName;
  }

  public void setLeftColumnName(String leftColumnName) {
    this.leftColumnName = leftColumnName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Relation relation = (Relation) o;
    return new EqualsBuilder()
        .append(leftTableName, relation.leftTableName)
        .append(leftColumnName, relation.leftColumnName)
        .append(rightTableName, relation.rightTableName)
        .append(rightColumnName, relation.rightColumnName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(validity)
        .append(leftTableName)
        .append(leftColumnName)
        .append(rightTableName)
        .append(rightColumnName)
        .toHashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "(%s) %s -> %s (%s)", leftTableName, leftColumnName, rightColumnName, rightTableName);
  }
}
