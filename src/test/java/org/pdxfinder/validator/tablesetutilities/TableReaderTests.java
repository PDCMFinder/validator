package org.pdxfinder.validator.tablesetutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pdxfinder.MockXssfWorkbook;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableReaderTests {

  private static final int SHEET_COUNT = 5;
  private FileInputStream fileInputStream;

  @Before
  public void buildWorkbookStream() throws IOException {
    File mockWorkbook = MockXssfWorkbook.createTempWorkbook(SHEET_COUNT);
    fileInputStream = new FileInputStream(mockWorkbook.getAbsolutePath());
  }

  @Test
  public void given_inputStreamOfExcelFile_when_readXlsx_returnAppropriateTable()
      throws IOException {
    List<Table> actualTable = TableReader.readXlsx(fileInputStream);
    Assert.assertEquals(SHEET_COUNT, actualTable.size());
  }

  @Test
  public void give_listOfTables_when_ListToMapIsCalled_ReturnList() throws IOException {
    List<Table> actualTable = TableReader.readXlsx(fileInputStream);
    Map<String, Table> actualMap = TableReader.listToMap(actualTable);
    actualTable.forEach(x -> Assert.assertTrue(actualMap.containsValue(x)));
  }

  @Test
  public void removeActualBlankRows_GivenTableWithBlankRowsInSecondColum_returnTableWithAllRows() {
    final String TABLE_NAME = "table1";
    final String ROW_VALUE = "row_value";
    List<String> columnValues =
        Arrays.asList("Header", "Header2", "Header3", "Header4", ROW_VALUE, ROW_VALUE, ROW_VALUE);
    List<String> columnValuesWithMissing =
        Arrays.asList("Header", "Header2", "Header3", "Header4", "", "", ROW_VALUE);
    Table tableWithBlanks =
        Table.create()
            .addColumns(
                StringColumn.create("column_1", columnValues),
                StringColumn.create("column_2", columnValuesWithMissing));
    Table expectedTable =
        Table.create(TABLE_NAME)
            .addColumns(
                StringColumn.create("column_1", ROW_VALUE, ROW_VALUE, ROW_VALUE),
                StringColumn.create("column_2", "", "", ROW_VALUE));
    Map<String, Table> actualTable =
        TableReader.cleanPdxTables(Map.of(TABLE_NAME, tableWithBlanks));
    Assert.assertEquals(expectedTable.toString(), actualTable.get(TABLE_NAME).toString());
  }
}
