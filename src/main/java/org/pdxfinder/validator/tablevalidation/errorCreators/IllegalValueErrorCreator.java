package org.pdxfinder.validator.tablevalidation.errorCreators;

import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.errorBuilders.IllegalValueErrorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class IllegalValueErrorCreator extends ErrorCreator {

  private static final Logger log = LoggerFactory.getLogger(IllegalValueErrorCreator.class);

  public List<ValidationError> generateErrors(
          Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    tableSetSpecification
            .getCharSetRestrictions()
        .forEach(
            (columns, valueRestriction) ->
                reportIllegalValue(columns, valueRestriction, tableSet));
    return errors;
  }

  public ValidationError create(
      String tableName,
      int count, String errorDescription,
      String invalidValues, String columnName) {
    return new IllegalValueErrorBuilder(tableName, columnName, count)
            .buildCause(invalidValues)
            .buildRule(errorDescription)
            .build();
  }

  private void reportIllegalValue(
      Set<ColumnReference> columns,
      ValueRestrictions valueRestrictions,
      Map<String, Table> tableSet) {
    for (ColumnReference columnReference : columns) {
      if (!tableMissingColumn(
          tableSet.get(columnReference.table()),
          columnReference.column(),
          columnReference.table())) {
        validateColumn(columnReference, valueRestrictions, tableSet);
      }
    }
  }

  private void validateColumn(
      ColumnReference columnReference,
      ValueRestrictions valueRestrictions,
      Map<String, Table> tableSet) {
    Table workingTable = tableSet.get(columnReference.table());
    StringColumn column = workingTable.column(columnReference.column()).asStringColumn();
    Predicate<String> testValues = valueRestrictions.getPredicate();
    Predicate<String> testEmptiness = valueRestrictions.getEmptyFilter();
    List<String> invalidValues =
        column.asList().stream()
            .filter(testValues)
            .filter(testEmptiness)
            .collect(Collectors.toCollection(LinkedList::new));
    if (!invalidValues.isEmpty()) {
      HashSet<String> uniqueInvalidValues = new HashSet<>(invalidValues);
      errors.add(
          create(
              columnReference.table(),
              invalidValues.size(),
              valueRestrictions.getErrorDescription(),
              uniqueInvalidValues.toString(),
              columnReference.column())
      );
    }
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
