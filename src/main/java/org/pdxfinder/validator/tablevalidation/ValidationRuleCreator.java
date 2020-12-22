package org.pdxfinder.validator.tablevalidation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ValidationRuleCreator {

  protected abstract TableSetSpecification generate();

  protected static Set<ColumnReference> matchingColumnsFromTable(
      Set<ColumnReference> columns, String tableName, String[] columnNamePatterns) {
    return columns.stream()
        .filter(c -> c.table().contains(tableName))
        .filter(c -> containsAny(c.column(), columnNamePatterns))
        .collect(Collectors.toSet());
  }

  protected static Set<ColumnReference> matchingColumnFromTable(
      Set<ColumnReference> columns, String tableName, String columnName) {
    return matchingColumnsFromTable(columns, tableName, new String[] {columnName});
  }

  protected static Set<ColumnReference> matchingColumnsFromAnyTable(
      Set<ColumnReference> columns, String columnNamePattern) {
    return columns.stream()
        .filter(c -> c.column().contains(columnNamePattern))
        .collect(Collectors.toSet());
  }

  private static boolean containsAny(String inputStr, String[] patterns) {
    return Arrays.stream(patterns).parallel().anyMatch(inputStr::equalsIgnoreCase);
  }
}
