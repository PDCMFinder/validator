package org.pdxfinder.validator.tableutilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.xlsx.XlsxReadOptions;


public class ValidationXlsxReaderTest {

  private List<Table> readN(String name, int expectedCount) {
    try {
      String fileName = name + ".xlsx";
      List<Table> tables =
          new ValidationXlsxReader()
              .readMultiple(XlsxReadOptions.builder("./data/" + fileName).build(), true);
      assertNotNull(tables, "No tables read from " + fileName);
      assertEquals(expectedCount, tables.size(), "Wrong number of tables in " + fileName);
      return tables;
    } catch (final IOException e) {
      fail(e.getMessage());
    }
    return null;
  }

  private Table read1(String name, int size, String... columnNames) {
    Table table = readN(name, 1).get(0);
    int colNum = 0;
    for (final Column<?> column : table.columns()) {
      assertEquals(columnNames[colNum], column.name(), "Wrong column name");
      assertEquals(size, column.size(), "Wrong size for column " + columnNames[colNum]);
      colNum++;
    }
    return table;
  }

  @SafeVarargs
  private final <T> void assertColumnValues(Column<T> column, T... ts) {
    for (int i = 0; i < column.size(); i++) {
      if (ts[i] == null) {
        assertTrue(
            column.isMissing(i),
            "Should be missing value in row "
                + i
                + " of column "
                + column.name()
                + ", but it was "
                + column.get(i));
      } else {
        assertEquals(
            ts[i], column.get(i), "Wrong value in row " + i + " of column " + column.name());
      }
    }
  }

  @Test
  void testColumns() {
    Table table =
        read1(
            "columns",
            2,
            "stringcol",
            "shortcol",
            "intcol",
            "longcol",
            "doublecol",
            "booleancol",
            "datecol",
            "formulacol");
    //        stringcol   shortcol    intcol  longcol doublecol   booleancol  datecol       formulacol
    //        Hallvard    123 12345678    12345678900 12,34   TRUE    22/02/2019 20:54:09   135.34
    //        Marit       124 12345679    12345678901 13,35   FALSE   23/03/2020 21:55:10   137.35
    assertColumnValues(table.stringColumn("stringcol"), "Hallvard", "Marit");
    assertColumnValues(table.intColumn("shortcol"), 123, 124);
    assertColumnValues(table.intColumn("intcol"), 12345678, 12345679);
    assertColumnValues(table.longColumn("longcol"), 12345678900L, 12345678901L);
    assertColumnValues(table.doubleColumn("doublecol"), 12.34, 13.35);
    assertColumnValues(table.dateTimeColumn("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9),
        LocalDateTime.of(2020, 3, 23, 21, 55, 10));
    assertColumnValues(table.doubleColumn("formulacol"), 135.34, 137.35);
  }

  @Test
  void testNumericToStringCoercion() {
    Table table =
        read1(
            "columnsmixed",
            4,
            "mixcol",
            "othercol");
    //        stringcol
    //        Hallvard
    //        0.0
    //        1.0
    assertColumnValues(table.stringColumn("mixcol"), "Hallvard", "0.0", "", "1.0");
  }

  @Test
  void testColumnsWithMissingValues() {
    Table table =
        read1(
            "columns-with-missing-values",
            2,
            "stringcol",
            "shortcol",
            "intcol",
            "longcol",
            "doublecol",
            "booleancol",
            "datecol",
            "formulacol");
    //        stringcol    shortcol    intcol        longcol        doublecol    booleancol
    // datecol
    //        Hallvard                12345678    12345678900                TRUE        22/02/2019
    // 20:54:09
    //                    124            12345679                13,35
    assertColumnValues(table.stringColumn("stringcol"), "Hallvard", null);
    assertColumnValues(table.intColumn("shortcol"), null, 124);
    assertColumnValues(table.intColumn("intcol"), 12345678, 12345679);
    assertColumnValues(table.longColumn("longcol"), 12345678900L, null);
    assertColumnValues(table.doubleColumn("doublecol"), null, 13.35);
    assertColumnValues(table.booleanColumn("booleancol"), true, null);
    assertColumnValues(table.dateTimeColumn("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9),
        null);
    assertColumnValues(table.doubleColumn("formulacol"), null, 137.35);

  }

  @Test
  void testSheetIndex() throws IOException {
    Table table =
        new ValidationXlsxReader()
            .read(XlsxReadOptions.builder("./data/multiplesheets.xlsx").sheetIndex(1).build());
    assertNotNull(table, "No table read from multiplesheets.xlsx");
    assertColumnValues(table.stringColumn("stringcol"), "John", "Doe");
    assertEquals("multiplesheets.xlsx#Sheet2", table.name(), "table name is different");

    Table tableImplicit =
        new ValidationXlsxReader()
            .read(XlsxReadOptions.builder("./data/multiplesheets.xlsx").build());
    // the table from the 2nd sheet should be picked up
    assertNotNull(tableImplicit, "No table read from multiplesheets.xlsx");

    try {
      new ValidationXlsxReader()
          .read(XlsxReadOptions.builder("./data/multiplesheets.xlsx").sheetIndex(0).build());
      fail("First sheet is empty, no table should be found");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new ValidationXlsxReader()
          .read(XlsxReadOptions.builder("./data/multiplesheets.xlsx").sheetIndex(5).build());
      fail("Only 2 sheets exist, no sheet 5");
    } catch (IndexOutOfBoundsException iobe) {
      // expected
    }
  }
}

