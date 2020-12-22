package org.pdxfinder.validator.tableSetUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pdxfinder.MockXssfWorkbook;
import tech.tablesaw.api.Table;

public class TableReaderTests {

  private static final int SHEET_COUNT = 5;
  private FileInputStream fileInputStream;

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

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

}
