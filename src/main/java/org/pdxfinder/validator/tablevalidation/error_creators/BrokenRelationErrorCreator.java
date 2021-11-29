package org.pdxfinder.validator.tablevalidation.error_creators;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;
import org.pdxfinder.validator.tablevalidation.error.BrokenRelationErrorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class BrokenRelationErrorCreator extends ErrorCreator {

  private static final Logger log = LoggerFactory.getLogger(BrokenRelationErrorCreator.class);

  public List<ValidationError> generateErrors(
          Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (Relation relation : tableSetSpecification.getRelations()) {
      reportRelationErrors(tableSet, relation, tableSetSpecification);
    }
    return errors;
  }

  public ValidationError create(
          String tableName, String columnName, Relation relation, String description) {
    return new BrokenRelationErrorBuilder(tableName, columnName)
            .buildCause(description)
            .buildRule(relation.getValidity().toString())
            .build();
  }

  private void reportRelationErrors(
          Map<String, Table> tableSet, Relation relation, TableSetSpecification tableSetSpecification) {
    if (!bothColumnsPresent(tableSet, relation)) {
      String errorMessage = String
              .format("Columns was not found in relation %s. Columns should be validated prior to checking relations", relation.toString());
      throw new IllegalStateException(errorMessage);
    }
    runAppropriateValidation(tableSet, relation, tableSetSpecification);
  }

  private void runAppropriateValidation(
      Map<String, Table> tableSet, Relation relation, TableSetSpecification tableSetSpecification) {
    RelationType validity = relation.getValidity();
    if (validity.equals(RelationType.TABLE_KEY)) {
      reportOrphanRowsWhenMissingValuesInRelation(tableSet, relation);
    } else if (validity.equals(RelationType.TABLE_KEY_MANY_TO_ONE)) {
      reportOneSidedOrphanedRosWhenMissingValuesInRelation(tableSet, relation);
    } else if (validity.equals(RelationType.ONE_TO_ONE)) {
      reportBrokenOneToOneRelation(tableSet, relation);
    } else if (validity.equals(RelationType.ONE_TO_MANY)) {
      reportBrokenOneToManySheetRelation(tableSet, relation);
    }
  }

  private void reportBrokenOneToOneRelation(
      Map<String, Table> tableSet, Relation relation) {
    ColumnReference leftRefColumn = relation.leftColumnReference();
    ColumnReference rightRefColumn = relation.getOtherColumn(leftRefColumn);
    StringColumn leftRestrictedColumn =
        tableSet.get(rightRefColumn.table()).stringColumn(leftRefColumn.column());
    StringColumn rightRestrictedColumn =
        tableSet.get(leftRefColumn.table()).stringColumn(rightRefColumn.column());
    Table workingTable = tableSet.get(leftRefColumn.table());
    int[] indexOfDuplicates =
        getIndexOfDuplicatedForPair(leftRestrictedColumn, rightRestrictedColumn);
    if (indexOfDuplicates.length > 0) {
      List<Pair<String, String>> brokenPairs =
          IntStream.of(indexOfDuplicates)
              .mapToObj(x -> Pair.of(leftRestrictedColumn.get(x), rightRestrictedColumn.get(x)))
              .collect(Collectors.toList());
      String description =
          String.format(
                  "%s invalid relationships: %s",
                  leftRefColumn.table(), brokenPairs.size(), brokenPairs.toString());
      errors.add(
              create(
                      leftRefColumn.table(),
                      leftRefColumn.column(),
                      relation,
                      description));
    }
  }

  private int[] getIndexOfDuplicatedForPair(
      StringColumn leftRestrictedColumn, StringColumn rightRestrictedColumn) {
    Set<Integer> leftIndexOfDuplicates = getIndexOfDuplicatedColumnValues(leftRestrictedColumn);
    Set<Integer> rightIndexOfDuplicates = getIndexOfDuplicatedColumnValues(rightRestrictedColumn);
    Set<Integer> allDuplicates = new HashSet<>();
    allDuplicates.addAll(leftIndexOfDuplicates);
    allDuplicates.addAll(rightIndexOfDuplicates);
    return unboxSet(allDuplicates);
  }

  private Set<Integer> getIndexOfDuplicatedColumnValues(StringColumn column) {
    return column.asList().stream()
        .filter(x -> column.countOccurrences(x) > 1)
        .map(x -> indicesOf(column, x))
        .flatMapToInt(Arrays::stream)
        .boxed()
        .collect(Collectors.toSet());
  }

  private int[] indicesOf(StringColumn column, String search) {
    return IntStream.range(0, column.size()).filter(i -> column.get(i).equals(search)).toArray();
  }

  private int[] unboxSet(Set<Integer> box) {
    return box.stream().mapToInt(x -> x).toArray();
  }

  private void reportBrokenOneToManySheetRelation(
      Map<String, Table> tableSet, Relation relation) {
    ColumnReference leftColumn = relation.leftColumnReference();
    ColumnReference rightColumn = relation.getOtherColumn(leftColumn);
    StringColumn oneRestrictedColumn =
        tableSet.get(rightColumn.table()).stringColumn(leftColumn.column());
    StringColumn manyRestrictedColumn =
        tableSet.get(leftColumn.table()).stringColumn(rightColumn.column());
    Table workingTable = tableSet.get(leftColumn.table());

    MultiValuedMap<String, Pair<String, String>> columnPairs = new HashSetValuedHashMap<>();
    for (int i = 0; i < manyRestrictedColumn.size(); i++) {
      columnPairs.put(
          oneRestrictedColumn.get(i),
          Pair.of(manyRestrictedColumn.get(i), oneRestrictedColumn.get(i)));
    }
    List<Pair<String, String>> listOfBrokenPairs =
        oneRestrictedColumn.asList().stream()
            .filter(x -> oneRestrictedColumn.countOccurrences(x) > 1)
            .map(columnPairs::get)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    if (!listOfBrokenPairs.isEmpty()) {
      int[] invalidRows = unboxSet(getIndexOfDuplicatedColumnValues(oneRestrictedColumn));
      String description =
          String.format(
                  "%s invalid relationship: %s",
                  listOfBrokenPairs.size(),
                  listOfBrokenPairs.toString());
      errors.add(
              create(
                      leftColumn.table(),
                      leftColumn.column(),
                      relation,
                      description));
    }
  }

  private void reportOrphanRowsWhenMissingValuesInRelation(
      Map<String, Table> tableSet, Relation relation) {
    reportOrphanRowsFor(tableSet, relation, relation.leftColumnReference());
    reportOrphanRowsFor(tableSet, relation, relation.rightColumnReference());
  }

  private void reportOneSidedOrphanedRosWhenMissingValuesInRelation(Map<String, Table> tableSet,
      Relation relation) {
    reportOrphanRowsFor(tableSet, relation, relation.leftColumnReference());
  }

  private void reportOrphanRowsFor(
      Map<String, Table> tableSet, Relation relation, ColumnReference child) {
    ColumnReference parent = relation.getOtherColumn(child);
    Table orphanTable =
        getTableOfOrphanRows(
            tableSet.get(child.table()),
            tableSet.get(child.table()).stringColumn(child.column()),
            tableSet.get(parent.table()).stringColumn(parent.column()));
    if (orphanTable.rowCount() > 0) {
      String description =
              String.format("%s values column %s in %s table are not found in this column", orphanTable.rowCount(), child.column(), child.table());
      errors.add(
              create(parent.table(), parent.column(), relation, description));
    }
  }

  private Table getTableOfOrphanRows(Table childTable, StringColumn child, StringColumn parent) {
    Set<String> parentSet = parent.asSet();
    return childTable.where(child.isNotIn(parentSet));
  }

  private boolean bothColumnsPresent(Map<String, Table> tableSet, Relation relation) {
    return (!missingLeftColumn(tableSet, relation) && !missingRightColumn(tableSet, relation));
  }

  private boolean missingLeftColumn(Map<String, Table> tableSet, Relation relation) {
    return tableMissingColumn(
        tableSet.get(relation.leftTable()), relation.leftColumn(), relation.leftTable());
  }

  private boolean missingRightColumn(Map<String, Table> tableSet, Relation relation) {
    return tableMissingColumn(
        tableSet.get(relation.rightTable()), relation.rightColumn(), relation.rightTable());
  }

  private boolean tableMissingColumn(Table table, String columnName, String tableName) {
    try {
      return !table.columnNames().contains(columnName);
    } catch (NullPointerException e) {
      log.error("Couldn't access table {} because of {}", tableName, e.toString());
      return true;
    }
  }
}
