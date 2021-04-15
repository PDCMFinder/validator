package org.pdxfinder.validator.tableutilities;

import static org.junit.Assert.assertEquals;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.cleanValues;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.removeDescriptionColumn;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.removeHeaderRows;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.removeHeaderRowsIfPresent;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.removeProviderNameFromFilename;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class TableSetUtilitiesTest {

  @Test
  public void removeHeaderRows_givenHeader_headerRemoved() {
    Map<String, Table> tableSet = new HashMap<>();
    Arrays.asList("table_1.tsv", "table_2.tsv")
        .forEach(
            s ->
                tableSet.put(
                    s,
                    Table.create()
                        .addColumns(
                            StringColumn.create(
                                "column_name",
                                Arrays.asList("Header2", "Header3", "Header4", "Header5", "1")))));
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList("table_1.tsv", "table_2.tsv")
        .forEach(
            s ->
                expected.put(
                    s,
                    Table.create()
                        .addColumns(
                            StringColumn.create("column_name", Collections.singletonList("1")))));
    assertEquals(expected.toString(), removeHeaderRows(tableSet).toString());
  }

  @Test
  public void removeHeaderRowsIfPresent_givenTableSet_runsOnEachTable() {
    Map<String, Table> tableSet = new HashMap<>();
    Arrays.asList("table_1.tsv", "table_2.tsv")
        .forEach(s -> tableSet.put(s, Table.create(s, Collections.emptyList())));
    Map<String, Table> expected = tableSet;
    assertEquals(expected, removeHeaderRowsIfPresent(tableSet));
  }

  @Test
  public void removeHeaderRowsIfPresent_givenHeadersPresent_removeHeaders() {
    Table table =
        Table.create()
            .addColumns(
                StringColumn.create(
                    "Field",
                    Arrays.asList("Description", "Example", "Format Requirements", "Essential?")),
                StringColumn.create(
                    "required_column",
                    Arrays.asList(
                        "required col description", "example value", "alphanumeric", "essential")));
    Table expected =
        Table.create()
            .addColumns(
                StringColumn.create("Field", Collections.emptyList()),
                StringColumn.create("required_column", Collections.emptyList()));
    assertEquals(expected.toString(), TableCleaner.removeHeaderRowsIfPresent(table).toString());
  }

  @Test
  public void removeHeaderRowsIfPresent_givenNoHeaders_returnTable() {
    Table table =
        Table.create()
            .addColumns(
                StringColumn.create("required_column", Arrays.asList("1", "2", "3", "4", "5")));
    Table expected = table;
    assertEquals(expected, TableCleaner.removeHeaderRowsIfPresent(table));
  }

  @Test
  public void removeDescriptionColumn_givenDescriptionColumn_removeDescriptionColumn() {
    Map<String, Table> tableSet = new HashMap<>();
    Arrays.asList("table_1.tsv")
        .forEach(
            s ->
                tableSet.put(
                    s,
                    Table.create()
                        .addColumns(StringColumn.create("Field", Collections.emptyList()))));
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList("table_1.tsv").forEach(s -> expected.put(s, Table.create()));
    removeDescriptionColumn(tableSet);
    assertEquals(expected.toString(), tableSet.toString());
  }

  @Test
  public void removeProviderNameFromFilename_givenProviderInFilename_stripProvider() {
    final String TABLE_NAME = "PROVIDER-BC_table_1.tsv";
    final String NEW_TABLE_NAME = "table_1.tsv";
    Map<String, Table> tableSet = new HashMap<>();
    Arrays.asList(TABLE_NAME).forEach(s -> tableSet.put(s, Table.create(TABLE_NAME)));
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList(NEW_TABLE_NAME).forEach(s -> expected.put(s, Table.create(NEW_TABLE_NAME)));
    assertEquals(expected.toString(), removeProviderNameFromFilename(tableSet).toString());
  }

  @Test
  public void removeProviderNameFromFilename_givenMultipleSep_stripProvider() {
    final String TABLE_NAME = "PROVIDER_123_table_1.tsv";
    final String NEW_TABLE_NAME = "123_table_1.tsv";
    Map<String, Table> tableSet = new HashMap<>();
    Arrays.asList(TABLE_NAME).forEach(s -> tableSet.put(s, Table.create(TABLE_NAME)));
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList(NEW_TABLE_NAME).forEach(s -> expected.put(s, Table.create(NEW_TABLE_NAME)));
    assertEquals(expected.toString(), removeProviderNameFromFilename(tableSet).toString());
  }

  @Test
  public void removeProviderNameFromFilename_givenNoSeparators_doNotStrip() {
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList("table.tsv").forEach(s -> expected.put(s, Table.create("table.tsv")));
    assertEquals(expected.toString(), removeProviderNameFromFilename(expected).toString());
  }

  @Test
  public void cleanSpacesAndLowerCase_givenTable_clean() {
    String exceptionColumn = "exception_column";
    List<Column<?>> tableColumns =
        Arrays.asList(
            StringColumn.create("column_1", Arrays.asList(" PADDEDSTRING ", " PADDEDSTRING_1 ")),
            StringColumn.create(
                exceptionColumn, Arrays.asList(" PADDEDSTRING ", " PADDEDSTRING_1 ")));
    List<Column<?>> expectedTableColumns =
        Arrays.asList(
            StringColumn.create("column_1", Arrays.asList("paddedstring", "paddedstring_1")),
            StringColumn.create(exceptionColumn, Arrays.asList("PADDEDSTRING", "PADDEDSTRING_1")));
    Map<String, Table> tableSet = new HashMap<>();
    Map<String, Table> expectedTableSet = new HashMap<>();
    Arrays.asList("table_1.tsv", "table_2.tsv")
        .forEach(s -> tableSet.put(s, Table.create(s, tableColumns)));
    Arrays.asList("table_1.tsv", "table_2.tsv")
        .forEach(s -> expectedTableSet.put(s, Table.create(s, expectedTableColumns)));

    assertEquals(
        expectedTableSet.toString(),
        cleanValues(tableSet, Collections.singletonList(exceptionColumn)).toString());
  }

  @Test
  public void cleanTablenames_givenHashMarkAndNewlines_clean() {
    List<Column<?>> tableColumns =
        Collections.singletonList(
            StringColumn.create("column_1", Collections.singletonList("test")));
    List<Column<?>> expectedColumns =
        Collections.singletonList(
            StringColumn.create("column_1", Collections.singletonList("test")));

    List<Table> tableSet = List.of(Table.create("#tableName\n\n", tableColumns));
    List<Table> expectedTableSet = List.of(Table.create("tableName", expectedColumns));

    assertEquals(
        expectedTableSet.get(0).name(), TableSetCleaner.cleanTableNames(tableSet).get(0).name());
  }

  @Test
  public void removeActualBlankRows_GivenTableWithBlankRowsInSecondColum_returnTableWithAllRows() {
    final String TABLE_NAME = "table1";
    final String ROW_VALUE = "row_value";
    final String ROW_VALUE2 = "row_value2";
    List<String> columnValues =
        Arrays.asList("Header", "Header2", "Header3", "Header4", ROW_VALUE, ROW_VALUE, ROW_VALUE2);
    List<String> columnValuesWithMissing =
        Arrays.asList("Header", "Header2", "Header3", "Header4", "", ROW_VALUE, "");
    Table tableWithBlanks =
        Table.create(TABLE_NAME)
            .addColumns(
                StringColumn.create("column_1", columnValues),
                StringColumn.create("column_2", columnValuesWithMissing));
    Table expectedTable =
        Table.create(TABLE_NAME)
            .addColumns(
                StringColumn.create("column_1", ROW_VALUE, ROW_VALUE, ROW_VALUE2),
                StringColumn.create("column_2", "", ROW_VALUE, ""));
    Map<String, Table> actualTable =
        TableSetCleaner.cleanPdxTables(Map.of(TABLE_NAME, tableWithBlanks));
    Assert.assertEquals(expectedTable.toString(), actualTable.get(TABLE_NAME).toString());
  }

  @Test
  public void given_UpdogeFilename_WhenCleanFilenamesIsCalled_TransformFilename() {
    String updogFile = "PROVIDER_metadata-sample.tsv";
    String expected = "sample";
    Map<String, Table> pdxFiles = Map.of(updogFile, Table.create());
    Assert.assertTrue(
        TableSetCleaner.cleanFileNames(pdxFiles)
            .keySet()
            .contains(expected)
    );
  }

  @Test
  public void given_xlsxGeneratedFilename_WhenCleanFilenamesIsCalled_TransformFilename() {
    String expected = "sample";
    Map<String, Table> pdxFiles = Map.of(expected, Table.create());
    Assert.assertTrue(
        TableSetCleaner.cleanFileNames(pdxFiles)
            .keySet()
            .contains(expected)
    );
  }
}

