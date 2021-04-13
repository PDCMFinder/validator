package org.pdxfinder.validator.tableutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pdxfinder.MockXssfWorkbook;
import tech.tablesaw.api.Table;

public class FileReaderTests {

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
    List<Table> actualTable = FileReader.readXlsx(fileInputStream);
    Assert.assertEquals(SHEET_COUNT, actualTable.size());
  }

  @Test
  public void given_listOfTables_when_ListToMapIsCalled_ReturnList() throws IOException {
    List<Table> actualTable = FileReader.readXlsx(fileInputStream);
    Map<String, Table> actualMap = FileReader.listToMap(actualTable);
    actualTable.forEach(x -> Assert.assertTrue(actualMap.containsValue(x)));
  }

  @Test
  public void given_SingleDirectoryArgument_WhenDirectoryExists_returnDirectoryUri()
      throws IOException {
    TemporaryFolder tempFolder = new TemporaryFolder();
    tempFolder.create();
    String rootDir = tempFolder
        .getRoot()
        .toString();
    Assert.assertEquals(
        rootDir,
        FileReader.getDirectories(rootDir).get(0)
    );
    tempFolder.delete();
  }

  @Test
  public void given_SingleDirectoryArgument_WhenDirectoryDoesNotExist_returnBlankList() {
    String aNonExistentLocation = "/a/fake/directory";
    Assert.assertEquals(
        new ArrayList<String>(),
        FileReader.getDirectories(aNonExistentLocation)
    );
  }

  @Test
  public void given_UpdogIsPassed_WhenGetDirectoriesIsCalled_ReturnAllFoldersWithinUpdog()
      throws IOException {
    List<String> expectedList = List.of("Provider1", "Provider2", "Provider3");
    TemporaryFolder tempFolder = new TemporaryFolder();
    tempFolder.create();
    File updogFolder = tempFolder.newFolder("UPDOG");
    tempFolder.newFile("UPDOG/" + expectedList.get(0));
    tempFolder.newFile("UPDOG/" + expectedList.get(1));
    tempFolder.newFile("UPDOG/" + expectedList.get(2));
    String rootDir = tempFolder
        .getRoot()
        .toString();
    Assert.assertEquals(
        expectedList,
        FileReader.getDirectories(rootDir + "/UPDOG")
    );

  }
}


