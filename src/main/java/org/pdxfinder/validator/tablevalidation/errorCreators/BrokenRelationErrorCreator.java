package org.pdxfinder.validator.tablevalidation.errorCreators;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;
import org.pdxfinder.validator.tablevalidation.errorBuilders.BrokenRelationErrorBuilder;
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
      reportRelationErrors(tableSet, relation);
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
          Map<String, Table> tableSet, Relation relation) {
    if (!bothColumnsPresent(tableSet, relation)) {
      String errorMessage = String
              .format("Columns was not found in relation %s. Columns should be validated prior to checking relations", relation.toString());
      throw new IllegalStateException(errorMessage);
    }
    runAppropriateValidation(tableSet, relation);
  }

  private void runAppropriateValidation(
          Map<String, Table> tableSet, Relation relation) {
    RelationType validity = relation.getValidity();
    if (validity.equals(RelationType.TABLE_KEY))
    //TABLE KEY compares two sets and reports what is missing for both sets. Generates an error for each column in the relation.
    //Uses sets and tolerates duplicate values
    {
      reportOrphanRowsWhenMissingValuesInRelation(tableSet, relation);
    } else if (validity.equals(RelationType.TABLE_KEY_MANY_TO_ONE)) {
      reportOneSidedOrphanedRosWhenMissingValuesInRelation(tableSet, relation);
      //Used for Omic data. Uses same implementation as TABLE_KEY, but only runs on oneside of the relationship.
      //takes of a set of a column and reports if they are represented in the other set.
    } else if (validity.equals(RelationType.ONE_TO_ONE))
    //Looks for duplicates in both columns of the relation. Do not use on columns that are not unique values
    //The duplicate column will be paired with the other column value and
    //printed. Could be improved by checking if the duplicate values are the same or different.
    {
      reportBrokenOneToOneRelation(tableSet, relation);
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
    int[] indexOfDuplicates = getIndexOfDuplicatedForPair(leftRestrictedColumn, rightRestrictedColumn);
    if (indexOfDuplicates.length > 0) {
      List<Pair<String, String>> brokenPairs = getSortedPairsFromIndex(indexOfDuplicates, leftRestrictedColumn, rightRestrictedColumn);
      String description =
              String.format(
                      "%s invalid relationships between column %s in table %s: %s",
                      brokenPairs.size(), rightRefColumn.column(), rightRefColumn.table(), brokenPairs);
      errors.add(
              create(
                      leftRefColumn.table(),
                      leftRefColumn.column(),
                      relation,
                      description));
    }
  }

  private List<Pair<String, String>> getSortedPairsFromIndex(int[] indexOfDuplicates, StringColumn leftRestrictedColumn, StringColumn rightRestrictedColumn) {
    MultiValuedMap<String, Pair<String, String>> leftPairsByValue;
    MultiValuedMap<String, Pair<String, String>> rightPairsByValue;
    leftPairsByValue = buildMultiValueMap(indexOfDuplicates, leftRestrictedColumn, leftRestrictedColumn, rightRestrictedColumn);
    rightPairsByValue = buildMultiValueMap(indexOfDuplicates, rightRestrictedColumn, leftRestrictedColumn, rightRestrictedColumn);
    var leftPairs = flattenPairsByValue(leftPairsByValue);
    var rightPairs = flattenPairsByValue(rightPairsByValue);
    leftPairs.addAll(rightPairs);
    return leftPairs;
  }

  private List<Pair<String, String>> flattenPairsByValue(MultiValuedMap<String, Pair<String, String>> pairsByValue) {
    return pairsByValue.entries().stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

  }

  private MultiValuedMap<String, Pair<String, String>> buildMultiValueMap(int[] indexOfDuplicates, StringColumn keyColumn, StringColumn leftRestrictedColumn, StringColumn rightRestrictedColumn) {
    MultiValuedMap<String, Pair<String, String>> pairsByValue = new HashSetValuedHashMap<>();
    for (int index : indexOfDuplicates) {
      pairsByValue.put(keyColumn.get(index), Pair.of(leftRestrictedColumn.get(index), rightRestrictedColumn.get(index)));
    }
    return pairsByValue;
  }

  private int[] getIndexOfDuplicatedForPair(
          StringColumn leftColumn, StringColumn rightColumn) {
    Set<Integer> leftIndexOfDuplicates = getIndexOfDuplicatedColumnValues(leftColumn);
    Set<Integer> rightIndexOfDuplicates = getIndexOfDuplicatedColumnValues(rightColumn);
    Set<Integer> duplicates = new HashSet<>();
    duplicates.addAll(leftIndexOfDuplicates);
    duplicates.addAll(rightIndexOfDuplicates);
    return unboxSet(duplicates);
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

  private void reportOrphanRowsWhenMissingValuesInRelation(
      Map<String, Table> tableSet, Relation relation) {
    reportOrphanRowsFor(tableSet, relation, relation.leftColumnReference());
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
      List<String> orphanedColumnList = (List<String>) orphanTable.column(relation.rightColumn()).asList();
      String orphanedColumnValues = StringUtils.join(orphanedColumnList, ", ");
      String description =
              String.format(
                      "%s values in column %s of the %s table are not found in this column: %s",
                      orphanTable.rowCount(),
                      child.column(),
                      child.table(),
                      orphanedColumnValues
              );
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
