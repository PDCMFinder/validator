package org.pdxfinder.validator.tableutilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.xlsx.XlsxReadOptions;

public class FileReader {

  private static final Logger log = LoggerFactory.getLogger(FileReader.class);

  private FileReader() {
    throw new IllegalStateException("Utility classs");
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
    return directoryList.stream()
        .map(directory -> String.format("%s/%s", updogRootFolder, directory))
        .collect(Collectors.toList());
  }

  public static List<Path> findAllFilesIn(Path targetDirectory, PathMatcher filter) {
    List<Path> matchingFiles = new ArrayList<>();
    try (final Stream<Path> stream = Files.walk(targetDirectory)) {
      matchingFiles = stream
          .filter(filter::matches)
          .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("There was an error reading the files", e);
    }
    return matchingFiles;
  }

  public static Map<String, Table> readAllTsvFilesIn(Path targetDirectory, PathMatcher filter) {
    HashMap<String, Table> tables = new HashMap<>();
    try {
      var matchingFiles = findAllFilesIn(targetDirectory, filter);
      for (Path matchingFile : matchingFiles) {
        Table matchingFileTable = readTsvOrReturnEmpty(matchingFile.toFile());
        String tablename = matchingFile.getFileName().toString();
        if (!matchingFileTable.isEmpty()) {
          tables.put(tablename, matchingFileTable);
        }
      }
    } catch (IndexOutOfBoundsException e) {
      log.error("Broken file detected", e);
    }
    return tables;
  }

  public static Table readTsvOrReturnEmpty(File file) {
    Table dataTable = Table.create();
    log.trace("Reading tsv file {}", file);
    try {
      if (file.exists() && file.isFile()) {
        dataTable = readTsv(file);
      }
    } catch (IOException | NullPointerException e) {
      var error = String.format("Error reading file %s Please check file content",
          file.getAbsolutePath());
      log.error(error);
    }
    return dataTable;
  }

  public static Table readTsv(File file) throws IOException {
    CsvReadOptions.Builder builder = CsvReadOptions.builder(file).sample(false).separator('\t');
    CsvReadOptions options = builder.build();
    return Table.read().usingOptions(options);
  }
}
