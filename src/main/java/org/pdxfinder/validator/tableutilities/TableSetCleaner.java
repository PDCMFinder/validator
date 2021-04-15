package org.pdxfinder.validator.tableutilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tech.tablesaw.api.Table;

public class TableSetCleaner {

  private TableSetCleaner() {
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
    TableSetCleaner.removeDescriptionColumn(pdxTableSet);
    pdxTableSet = TableSetCleaner.removeHeaderRows(pdxTableSet);
    pdxTableSet = TableSetCleaner.cleanValues(pdxTableSet, columnsExemptFromLowercasing);
    pdxTableSet = TableSetCleaner.removeProviderNameFromFilename(pdxTableSet);
    pdxTableSet = TableSetCleaner.removeFilenameArtifacts(pdxTableSet);
    return pdxTableSet;
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


  static Map<String, Table> removeProviderNameFromFilename(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> TableCleaner.substringAfterIfContainsSeparator(e.getKey(), "_"),
                e -> e.getValue()
                    .setName(TableCleaner.substringAfterIfContainsSeparator(e.getKey(), "_"))));
  }

  public static Map<String, Table> cleanFileNames(Map<String, Table> tableSet) {
    var cleanedTableSet = TableSetCleaner.removeProviderNameFromFilename(tableSet);
    return TableSetCleaner.removeFilenameArtifacts(cleanedTableSet);
  }

  static Map<String, Table> removeFilenameArtifacts(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> e.getKey().replaceAll("(metadata-|.tsv)", ""),
                e -> e.getValue().setName(e.getKey().replaceAll("(metadata-|.tsv)", ""))));
  }

  public static List<Table> cleanTableNames(List<Table> tables) {
    return tables.stream()
        .map(e -> e.setName(TableCleaner.removeHashmarksAndNewlines(e)))
        .collect(Collectors.toList());
  }

  static Map<String, Table> removeHeaderRowsIfPresent(Map<String, Table> tableSet) {
    return tableSet.entrySet().stream()
        .collect(Collectors
            .toMap(Map.Entry::getKey, e -> TableCleaner.removeHeaderRowsIfPresent(e.getValue())));
  }

  static void removeDescriptionColumn(Map<String, Table> tableSet) {
    tableSet.values().forEach(t -> TableCleaner.removeColumnIfExists(t, "Field"));
  }
}
