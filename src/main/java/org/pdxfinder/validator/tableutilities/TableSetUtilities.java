package org.pdxfinder.validator.tableutilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.Table;

public class TableSetUtilities {

  private TableSetUtilities() {
    throw new IllegalStateException("Utility class");
  }

  public static Map<String, Table> cleanPdxTables(Map<String, Table> pdxTableSet) {
    List<String> columnsExemptFromLowercasing =
        Arrays.asList(
            "model_id",
            "sample_id",
            "patient_id",
            "name",
            "validation_host_strain_full",
            "provider_name",
            "provider_abbreviation",
            "project",
            "internal_url",
            "internal_dosing_url");
    TableSetUtilities.removeDescriptionColumn(pdxTableSet);
    pdxTableSet = TableSetUtilities.removeHeaderRows(pdxTableSet);
    pdxTableSet = TableSetUtilities.cleanValues(pdxTableSet, columnsExemptFromLowercasing);
    return pdxTableSet;
  }


  public static List<Table> cleanTableNames(List<Table> tables) {
    return tables.stream()
        .map(e -> e.setName(removeHashmarksAndNewlines(e)))
        .collect(Collectors.toList());
  }

  private static String removeHashmarksAndNewlines(Table table) {
    return (table.name() != null) ? table.name().replaceAll("#|\\n", "") : "";
  }

  public static Map<String, Table> removeHeaderRows(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, e -> TableUtilities.removeHeaderRows(e.getValue(), 4)));
  }

  static Map<String, Table> removeHeaderRowsIfPresent(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> removeHeaderRowsIfPresent(e.getValue())));
  }

  static Table removeHeaderRowsIfPresent(Table table) {
    return table.columnNames().contains("Field")
        ? TableUtilities.removeHeaderRows(table, 4)
        : table;
  }

  static void removeDescriptionColumn(Map<String, Table> tableSet) {
    tableSet.values().forEach(t -> removeColumnIfExists(t, "Field"));
  }

  static void removeColumnIfExists(Table table, String columnToRemove) {
    if (table.columnNames().contains(columnToRemove)) table.removeColumns(columnToRemove);
  }

  @Deprecated
  static Map<String, Table> removeProviderNameFromFilename(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> substringAfterIfContainsSeparator(e.getKey(), "_"),
                e -> e.getValue().setName(substringAfterIfContainsSeparator(e.getKey(), "_"))));
  }

  static Map<String, Table> cleanValues(
      Map<String, Table> tableSet, List<String> exceptionColumns) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e ->
                    TableUtilities.cleanTableValues(
                        e.getValue(), e.getValue().name(), exceptionColumns)));
  }

  static String substringAfterIfContainsSeparator(String string, String separator) {
    return string.contains(separator) ? StringUtils.substringAfter(string, separator) : string;
  }

  public static <T> Set<T> concatenate(Set<T>... sets) {
    return Stream.of(sets).flatMap(Set::stream).collect(Collectors.toSet());
  }
}
