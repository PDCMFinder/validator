package org.pdxfinder.validator.tableutilities;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;

public class TableUtilities {

  private static final Logger log = LoggerFactory.getLogger(TableUtilities.class);

  private TableUtilities() {
  }

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

  public static Map<String, Table> mergeTableMaps(Map<String, Table> map1,
      Map<String, Table> map2) {
    var keyset1 = map1.keySet();
    if (map2.keySet().stream().anyMatch(key -> keyset1.contains(key))) {
      throw new IllegalStateException("Namespace error: two tables contain the same name");
    }
    map1.putAll(map2);
    return map1;
  }

  public static <T> Set<T> concatenate(Set<T>... sets) {
    return Stream.of(sets).flatMap(Set::stream).collect(Collectors.toSet());
  }
}
