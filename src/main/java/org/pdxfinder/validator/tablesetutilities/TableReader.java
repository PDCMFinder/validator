package org.pdxfinder.validator.tablesetutilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class TableReader {

  private TableReader() {}

  public static List<Table> readXlsx(InputStream inputStream) throws IOException {
    Source source = new Source(inputStream);
    XlsxReadOptions options = XlsxReadOptions.builder(source).build();
    XlsxReader reader = new XlsxReader();
    return reader.readMultiple(options);
  }

  public static Map<String, Table> listToMap(List<Table> tableList) {
    var tableSet = new HashMap<String, Table>();
    tableList.forEach(x -> tableSet.put(x.name(), x));
    return tableSet;
  }

  public static Map<String, Table> cleanPdxTables(Map<String, Table> pdxTableSet) {
    List<String> columnsExceptFromLowercasing =
        Arrays.asList(
            "model_id",
            "sample_id",
            "patient_id",
            "name",
            "validation_host_strain_full",
            "provider_name",
            "provider_abbreviation",
            "project",
            "internal_url",
            "internal_dosing_url");
    TableSetUtilities.removeDescriptionColumn(pdxTableSet);
    pdxTableSet = TableSetUtilities.removeHeaderRows(pdxTableSet);
    pdxTableSet = TableSetUtilities.cleanValues(pdxTableSet, columnsExceptFromLowercasing);
    return pdxTableSet;
  }
}
