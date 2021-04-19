package org.pdxfinder.validator.tableutilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import tech.tablesaw.api.Table;

public class TableSetCleaner {

  private TableSetCleaner() {
    throw new IllegalStateException("Utility class");
  }

  private static final List<String> defaultColumnsExemptFromLowercasing =
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


  public static Map<String, Table> cleanPdxTables(Map<String, Table> pdxTableSet) {
    removeDescriptionColumn(pdxTableSet);
    pdxTableSet = removeHeaderRows(pdxTableSet);
    pdxTableSet = cleanValues(pdxTableSet, defaultColumnsExemptFromLowercasing);
    pdxTableSet = cleanTableNames(pdxTableSet);
    return pdxTableSet;
  }

  static Map<String, Table> cleanTableNames(Map<String, Table> tableSet) {
    tableSet = applyFunctionToTableNames(tableSet,
        TableCleaner.substringAfterIfContainsSeparator("_"));
    tableSet = applyFunctionToTableNames(tableSet, replaceAll("(metadata-|.tsv)", ""));
    return applyFunctionToTableNames(tableSet, TableCleaner.removeHashmarksAndNewlines());
  }


  private static Map<String, Table> applyFunctionToTableNames(Map<String, Table> tableSet,
      UnaryOperator<String> tableNameFunction) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> tableNameFunction.apply(e.getKey()),
                e -> e.getValue().setName(tableNameFunction.apply(e.getKey()))));

  }

  public static Map<String, Table> removeHeaderRows(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, e -> TableCleaner.removeHeaderRows(e.getValue(), 4)));
  }

  static Map<String, Table> cleanValues(
      Map<String, Table> tableSet, List<String> exceptionColumns) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e ->
                    TableCleaner.cleanTableValues(
                        e.getValue(), e.getValue().name(), exceptionColumns)));
  }

  private static UnaryOperator<String> replaceAll(String regex, String replacement) {
    return string -> string.replaceAll(regex, replacement);
  }

  private static void removeDescriptionColumn(Map<String, Table> tableSet) {
    tableSet.values().forEach(t -> TableCleaner.removeColumnIfExists(t, "Field"));
  }
}
