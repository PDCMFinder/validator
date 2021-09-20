package org.pdxfinder.validator.tablevalidation.error;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;

public class DuplicateValueErrorReportTest {
  private DuplicateValueErrorCreator duplicateValueErrorCreator = new DuplicateValueErrorCreator();
  private String PROVIDER = "provider";

  @Test
  public void message() {
    String expected = "Error in [table] for provider [provider]: Duplicates found : [a]";
    ColumnReference uniqueColumn = ColumnReference.of("table", "column");
    Set<String> duplicateValues = new HashSet<>(Arrays.asList("a"));

    DuplicateValueError error =
        duplicateValueErrorCreator.create(uniqueColumn, duplicateValues, PROVIDER);

    assertEquals(expected, error.verboseMessage());
  }
}
