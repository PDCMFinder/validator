package org.pdxfinder.validator.tableutilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    String rootDir = tempFolder.getRoot().toString();
    Assert.assertEquals(rootDir, FileReader.getDirectories(rootDir).get(0));
    tempFolder.delete();
  }

  @Test
  public void given_SingleDirectoryArgument_WhenDirectoryDoesNotExist_returnBlankList() {
    String aNonExistentLocation = "/a/fake/directory";
    Assert.assertEquals(new ArrayList<String>(), FileReader.getDirectories(aNonExistentLocation));
  }

  @Test
  public void given_UpdogIsPassed_WhenGetDirectoriesIsCalled_ReturnAllFoldersWithinUpdog()
      throws IOException {
    TemporaryFolder tempFolder = new TemporaryFolder();
    tempFolder.create();
    File updogFolder = tempFolder.newFolder("UPDOG");
    List<String> expectedList =
        List.of(
            String.format("%s/%s", updogFolder, "Provider3"),
            String.format("%s/%s", updogFolder, "Provider2"),
            String.format("%s/%s", updogFolder, "Provider1"));
    new File(expectedList.get(0)).createNewFile();
    new File(expectedList.get(1)).createNewFile();
    new File(expectedList.get(2)).createNewFile();
    String rootDir = tempFolder.getRoot().toString();
    Assert.assertEquals(expectedList, FileReader.getDirectories(rootDir + "/UPDOG"));
    tempFolder.delete();
  }

  @Test
  public void given_FileDoesNotExists_When_readTsvOrReturnEmptyIsCalled_ReturnBlank() {
    File blankFile = new File("/a/fake/file");
    Assert.assertEquals("\n", FileReader.readTsvOrReturnEmpty(blankFile).toString());
  }

  @Test
  public void given_DirectoryWithFile_WhenReadAllTsvIsCalled_ReturnAllFiles() throws IOException {
    String wildCardFilter = "glob:**";
    TemporaryFolder folder = new TemporaryFolder();
    folder.create();
    String tableName1 = "model";
    String tableName2 = "sample";
    String tableName3 = "patient";
    File fileOne = folder.newFile(tableName1);
    File fileTwo = folder.newFile(tableName2);
    File fileThree = folder.newFile(tableName3);
    writeTestHeaderToFile(fileOne);
    writeTestHeaderToFile(fileTwo);
    writeTestHeaderToFile(fileThree);
    Path targetPath = Path.of(folder.getRoot().getAbsolutePath());
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher(wildCardFilter);
    Map<String, Table> tableSet = FileReader.readAllTsvFilesIn(targetPath, matcher);
    Set<String> tableNameSet = tableSet.keySet();
    Assert.assertTrue(tableNameSet.contains(tableName1));
    Assert.assertTrue(tableNameSet.contains(tableName2));
    Assert.assertTrue(tableName3.contains(tableName3));
    folder.delete();
  }

  private void writeTestHeaderToFile(File file) throws IOException {
    Writer output;
    output = new BufferedWriter(new FileWriter(file)); // clears file every time
    output.append("test\theader");
    output.close();
  }
}
