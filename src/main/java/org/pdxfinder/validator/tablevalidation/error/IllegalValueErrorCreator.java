package org.pdxfinder.validator.tablevalidation.error;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

@Component
public class IllegalValueErrorCreator extends ErrorCreator {

  private static final Logger log = LoggerFactory.getLogger(IllegalValueErrorCreator.class);

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    String provider = tableSetSpecification.getProvider();
    tableSetSpecification
        .getCharSetRestrictions()
        .forEach(
            (columns, valueRestriction) ->
                reportIllegalValue(columns, valueRestriction, tableSet, provider));
    return errors;
  }

  public IllegalValueError create(
      String tableName,
      String errorDescription,
      String columnName,
      Table invalidRows,
      String provider) {
    return new IllegalValueError(tableName, errorDescription, columnName, invalidRows, provider);
  }

  private void reportIllegalValue(
      Set<ColumnReference> columns,
      ValueRestrictions valueRestrictions,
      Map<String, Table> tableSet,
      String provider) {
    for (ColumnReference columnReference : columns) {
      if (!tableMissingColumn(
          tableSet.get(columnReference.table()),
          columnReference.column(),
          columnReference.table())) {
        validateColumn(columnReference, valueRestrictions, tableSet, provider);
      }
    }
  }

  private void validateColumn(
      ColumnReference columnReference,
      ValueRestrictions valueRestrictions,
      Map<String, Table> tableSet,
      String provider) {
    Table workingTable = tableSet.get(columnReference.table());
    StringColumn column = workingTable.column(columnReference.column()).asStringColumn();
    Predicate<String> testValues = valueRestrictions.getPredicate();
    Predicate<String> testEmptiness = valueRestrictions.getEmptyFilter();
    List<String> invalidValues =
        column.asList().stream()
            .filter(testValues)
            .filter(testEmptiness)
            .collect(Collectors.toCollection(LinkedList::new));
    int[] indexOfInvalids =
        invalidValues.stream()
            .map(column::indexOf)
            .flatMapToInt(x -> IntStream.of(x.intValue()))
            .toArray();

    if (!invalidValues.isEmpty()) {

      HashSet<String> uniqueInvalidValues = new HashSet<>(invalidValues);
      String errorDescriptions =
          IllegalValueError.buildDescription(
              invalidValues.size(),
              valueRestrictions.getErrorDescription(),
              uniqueInvalidValues.toString());
      errors.add(
          create(
              columnReference.table(),
              errorDescriptions,
              columnReference.column(),
              workingTable.rows(indexOfInvalids),
              provider)
              .getValidationError());
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
