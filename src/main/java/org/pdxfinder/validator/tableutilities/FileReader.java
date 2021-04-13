package org.pdxfinder.validator.tableutilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.xlsx.XlsxReadOptions;

public class FileReader {

  private static final Logger log = LoggerFactory.getLogger(FileReader.class);


  private FileReader() {
  }

  public static List<Table> readXlsx(InputStream inputStream) throws IOException {
    Source source = new Source(inputStream);
    XlsxReadOptions options = XlsxReadOptions.builder(source).build();
    ValidationXlsxReader reader = new ValidationXlsxReader();
    return reader.readMultiple(options, false);
  }

  public static Map<String, Table> listToMap(List<Table> tableList) {
    var tableSet = new HashMap<String, Table>();
    tableList.forEach(x -> tableSet.put(x.name(), x));
    return tableSet;
  }

  public static List<String> getDirectories(String fileDir) {
    List<String> directories = new ArrayList<>();
    File targetDir = Paths.get(fileDir).toFile();
    if (targetDir.isDirectory()) {
      directories = parseDirectories(targetDir);
    } else {
      log.error("Passed value is not a directory");
    }
    return directories;
  }

  private static List<String> parseDirectories(File targetDir) {
    List<String> directoryList;
    if (targetDir.getName().equalsIgnoreCase("UPDOG")) {
      directoryList = getUpdogDirectories(targetDir);
    } else {
      directoryList = List.of(targetDir.getAbsolutePath());
    }
    return directoryList;
  }

  private static List<String> getUpdogDirectories(File updogRootFolder) {
    List<String> directoryList;
    try {
      directoryList = Arrays.asList(Objects.requireNonNull(updogRootFolder.list()));
    } catch (NullPointerException nullPointer) {
      log.error("UPDOG directory is empty. What's UPDOG?");
      directoryList = new ArrayList<>();
    }
    return directoryList;
  }


}
