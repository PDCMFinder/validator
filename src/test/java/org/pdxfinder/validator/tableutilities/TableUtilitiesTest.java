package org.pdxfinder.validator.tableutilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableUtilitiesTest {

  @Test
  public void fromString_createTableWithOneColumn_matchesTableSawConstruction() {
    Table table = TableUtilities.fromString("table_name", "column_1", "value_1");
    Table table2 = Table.create("table_name", StringColumn.create("column_1", "value_1"));
    assertEquals(table.toString(), table2.toString());
  }

  @Test
  public void fromString_createTableWithTwoColumns_matchesTableSawConstruction() {
    Table table = TableUtilities.fromString("table_name", "column_1, column_2", "value_1, value_2");
    Table table2 =
        Table.create(
            "table_name",
            StringColumn.create("column_1", "value_1"),
            StringColumn.create("column_2", "value_2"));
    assertEquals(table.toString(), table2.toString());
  }
}
