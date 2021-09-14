package org.pdxfinder.validator.tablevalidation;

import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;

public class RelationTestUtilities {

  public static Relation betweenTableKeys(ColumnReference left, ColumnReference right) {
    if (left.equals(right)) {
      throw new IllegalArgumentException(
          String.format("Unable to define a relation from a column to itself (%s)", left));
    }

    return relationOf(
        RelationType.TABLE_KEY, left.table(), left.column(), right.table(), right.column());
  }

  public static Relation betweenTableColumns(
      RelationType plurality, ColumnReference left, ColumnReference right) {
    if (left.equals(right)) {
      throw new IllegalArgumentException(
          String.format("Unable to define a relation from a column to itself (%s)", left));
    }

    return relationOf(plurality, left.table(), left.column(), right.table(), right.column());
  }

  public static Relation relationOf(RelationType validity, String leftTableName,
      String leftColumnName, String rightTableName, String rightColumnName) {
    return new Relation(validity.name(), "", rightTableName, rightColumnName).addLeftTableAndColumn(
        leftTableName, leftColumnName);
  }


}
