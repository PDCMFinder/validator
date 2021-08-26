package org.pdxfinder.validator.tableutilities;

import static org.junit.Assert.assertEquals;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.cleanValues;
import static org.pdxfinder.validator.tableutilities.TableSetCleaner.removeHeaderRows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

class TableSetCleanerTest {

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
    Arrays.asList("table_1.tsv").forEach(s -> tableSet.put(s, createTestTable()));
    Map<String, Table> expected = new HashMap<>();
    Arrays.asList("table_1.tsv").forEach(s -> expected.put(s, createTestTable()));
    TableSetCleaner.cleanPdxTables(tableSet);
    assertEquals(expected.toString(), tableSet.toString());
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

    Map<String, Table> tableSet =
        Map.of("#tableName\n\n", Table.create("#tableName\n\n", tableColumns));
    Map<String, Table> expectedTableSet =
        Map.of("tableName", Table.create("tableName", expectedColumns));

    assertEquals(
        expectedTableSet.keySet().toArray(),
        TableSetCleaner.cleanTableNames(tableSet).keySet().toArray());
  }

  @Test
  public void removeActualBlankRows_GivenTableWithBlankRowsInSecondColum_returnTableWithAllRows() {
    final String TABLE_NAME = "table1";
    final String ROW_VALUE = "row_value";
    final String ROW_VALUE2 = "row_value2";
    List<String> columnValues =
        Arrays.asList("Header", "Header2", "Header3", "Header4", ROW_VALUE2, ROW_VALUE, ROW_VALUE);
    List<String> columnValuesWithMissing =
        Arrays.asList("Header", "Header2", "Header3", "Header4", "", "", ROW_VALUE);
    Table tableWithBlanks =
        Table.create(TABLE_NAME)
            .addColumns(
                StringColumn.create("column_1", columnValues),
                StringColumn.create("column_2", columnValuesWithMissing));
    Table expectedTable =
        Table.create(TABLE_NAME)
            .addColumns(
                StringColumn.create("column_1", ROW_VALUE2, ROW_VALUE, ROW_VALUE),
                StringColumn.create("column_2", "", "", ROW_VALUE));
    Map<String, Table> actualTable =
        TableSetCleaner.cleanPdxTables(Map.of(TABLE_NAME, tableWithBlanks));
    Assert.assertEquals(expectedTable.toString(), actualTable.get(TABLE_NAME).toString());
  }

  @Test
  void GivenTable_returnTableWithAllRows() {
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

  @ParameterizedTest
  @CsvSource({
      "PROVIDER_metadata-sample.tsv,sample",
      "sample,sample"
  })
  void given_variousFilenames_WhenCleanFilenamesIsCalled_TransformFilenameAppropriately(
      String testFilename, String expected) {
    Map<String, Table> pdxFiles = Map.of(testFilename, createTestTable());
    Assert.assertTrue(TableSetCleaner.cleanPdxTables(pdxFiles).containsKey(expected));
  }

  private Table createTestTable() {
    return TableUtilities.fromString("table_name", "column_1, column_2", "value_1, value_2");
  }
}
