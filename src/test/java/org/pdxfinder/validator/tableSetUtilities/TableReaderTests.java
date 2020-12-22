package org.pdxfinder.validator.tableSetUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pdxfinder.validator.tableSetUtilities.TableReader;
import tech.tablesaw.api.Table;

public class TableReaderTests {

  private static final int SHEET_COUNT = 5;
  private FileInputStream fileInputStream;

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void buildWorkbookStream() throws IOException {
    fileInputStream = buildWorkbookInputStream(SHEET_COUNT);
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

  private FileInputStream buildWorkbookInputStream(int sheetCount) throws IOException {
    String fileURI = tmpFolder.newFile().getAbsolutePath();
    XSSFWorkbook workbook = createWorkbook(sheetCount);
    saveWorkbook(fileURI, workbook);
    return new FileInputStream(new File(fileURI));
  }

  private XSSFWorkbook createWorkbook(int sheetCount) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    for (int i = 0; i < sheetCount; i++) {
      XSSFSheet sheet = workbook.createSheet();
      sheet.createRow(0).createCell(0).setCellValue("test");
    }
    return workbook;
  }

  private void saveWorkbook(String fileURI, XSSFWorkbook workbook) throws IOException {
    FileOutputStream out = new FileOutputStream(fileURI);
    workbook.write(out);
  }
}
