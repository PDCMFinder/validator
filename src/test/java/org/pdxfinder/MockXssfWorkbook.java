package org.pdxfinder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MockXssfWorkbook {

  private MockXssfWorkbook(){}

  public static File createTempWorkbook(int sheetCount) throws IOException {
    File tempWorkbook = File.createTempFile("temp", ".xlsx");
    tempWorkbook.deleteOnExit();
    XSSFWorkbook workbook = createWorkbook(sheetCount);
    saveWorkbook(tempWorkbook.getAbsolutePath(), workbook);
    return tempWorkbook;
  }

  private static XSSFWorkbook createWorkbook(int sheetCount) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    for (int i = 0; i < sheetCount; i++) {
      XSSFSheet sheet = workbook.createSheet();
      sheet.createRow(0).createCell(0).setCellValue("test");
    }
    return workbook;
  }

  private static void saveWorkbook(String fileURI, XSSFWorkbook workbook) throws IOException {
    FileOutputStream out = new FileOutputStream(fileURI);
    workbook.write(out);
  }
}
