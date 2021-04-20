package org.pdxfinder.validator.tableutilities;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;

public class TableUtilities {

  private static final Logger log = LoggerFactory.getLogger(TableUtilities.class);

  public static Table fromString(String tableName, String... lines) {
    Table table = Table.create();
    String string = String.join("\n", lines);
    try {
      table = Table.read().csv(IOUtils.toInputStream(string));
      table.setName(tableName);
    } catch (Exception e) {
      log.error("There was an error parsing string to Table", e);
    }
    return table;
  }

  public static <T> Set<T> concatenate(Set<T>... sets) {
    return Stream.of(sets).flatMap(Set::stream).collect(Collectors.toSet());
  }
}
