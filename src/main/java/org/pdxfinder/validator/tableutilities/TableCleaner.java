package org.pdxfinder.validator.tableutilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public final class TableCleaner {

  private static final Logger log = LoggerFactory.getLogger(TableCleaner.class);

  private TableCleaner() {
    throw new IllegalStateException("Utility class");
  }

  public static Table cleanTableValues(
      Table table, String tableName, List<String> columnExceptions) {
    Table lowerCasedTable = lowerCaseSelectColumnValues(table, columnExceptions);
    Table cleanedTable = trimColumnValues(lowerCasedTable);
    Table removedBlanks = removeEmptyRows(cleanedTable);
    removedBlanks.setName(tableName);
    return removedBlanks;
  }

  public static Table removeHeaderRows(Table table, int numberOfRows) {
    return doesNotHaveEnoughRows(table, numberOfRows)
        ? table.emptyCopy()
        : table.dropRange(numberOfRows);
  }

  private static Table removeEmptyRows(Table table) {
    return table.dropWhere(getEmptyRows(table));
  }

  private static Selection getEmptyRows(Table table) {
    int[] rowsToDrop = table.stream()
        .filter(TableCleaner::isRowBlank)
        .mapToInt(Row::getRowNumber)
        .toArray();
    return new BitmapBackedSelection(rowsToDrop);
  }

  private static boolean isRowBlank(Row row) {

    return row.columnNames().stream()
        .map(row::isMissing)
        .filter(isNotMissing())
        .findAny()
        .orElse(true);
  }

  private static Predicate<Boolean> isNotMissing() {
    return isEmpty -> !isEmpty;
  }

  private static Table trimColumnValues(Table table) {
    List<String> columnNames = table.columnNames();
    Table transformedTable =
        Table.create(
            table.columns().stream()
                .map(x -> x.asStringColumn().trim())
                .collect(Collectors.toList()));
    return renameColumnsInTable(transformedTable, columnNames);
  }

  public static Table lowerCaseSelectColumnValues(Table table, List<String> columnExceptions) {
    List<String> columnNames = table.columnNames();
    List<Column<?>> transformedColumns = new ArrayList<>();
    for (Column<?> column : table.columns()) {
      if (!columnExceptions.contains(column.name())) {
        Column<String> lowerCasedColumn = column.asStringColumn().lowerCase();
        transformedColumns.add(lowerCasedColumn);
      } else {
        transformedColumns.add(column);
      }
    }
    return renameColumnsInTable(Table.create(transformedColumns), columnNames);
  }

  private static Table renameColumnsInTable(Table table, List<String> newNames) {
    for (int i = 0; i < newNames.size(); i++) {
      table.column(i).setName(newNames.get(i));
    }
    return table;
  }

  private static boolean doesNotHaveEnoughRows(Table table, int numberOfRows) {
    return table.rowCount() <= numberOfRows;
  }

  public static Table removeHeaderRowsIfPresent(Table table) {
    return table.columnNames().contains("Field")
        ? TableCleaner.removeHeaderRows(table, 4)
        : table;
  }

  public static void removeColumnIfExists(Table table, String columnToRemove) {
    if (table.columnNames().contains(columnToRemove)) {
      table.removeColumns(columnToRemove);
    }
  }

  public static Function<String, String> removeHashmarksAndNewlines() {
    return (tableName -> (tableName != null) ? tableName.replaceAll("[#\\n]", "") : "");
  }

  public static Function<String, String> substringAfterIfContainsSeparator(String separator) {
    return string -> string.contains(separator) ? StringUtils.substringAfter(string, separator)
        : string;
  }
}
