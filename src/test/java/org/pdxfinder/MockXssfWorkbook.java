package org.pdxfinder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MockXssfWorkbook {

  private static final List<String> testNames =
      List.of("test0", "test1", "test2", "test3", "test4");
  private static final List<String> standardNames =
      List.of("patient", "sample", "model", "model_validation", "sharing");

  private MockXssfWorkbook() {
  }

  public static File createStandardizedTempWorkbook(int sheetCount) throws IOException {
    File tempWorkbook = File.createTempFile("temp", ".xlsx");
    tempWorkbook.deleteOnExit();
    XSSFWorkbook workbook = createWorkbook(sheetCount, standardNames);
    saveWorkbook(tempWorkbook.getAbsolutePath(), workbook);
    return tempWorkbook;
  }

  public static File createTempWorkbook(int sheetCount) throws IOException {
    File tempWorkbook = File.createTempFile("temp", ".xlsx");
    tempWorkbook.deleteOnExit();
    XSSFWorkbook workbook = createWorkbook(sheetCount, testNames);
    saveWorkbook(tempWorkbook.getAbsolutePath(), workbook);
    return tempWorkbook;
  }

  private static XSSFWorkbook createWorkbook(int sheetCount, List<String> sheetNames) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    for (int i = 0; i < sheetCount; i++) {
      XSSFSheet sheet = workbook.createSheet(sheetNames.get(i));
      sheet.createRow(0).createCell(0).setCellValue("test_data");
    }
    return workbook;
  }

  private static void saveWorkbook(String fileURI, XSSFWorkbook workbook) throws IOException {
    FileOutputStream out = new FileOutputStream(fileURI);
    workbook.write(out);
  }
}
