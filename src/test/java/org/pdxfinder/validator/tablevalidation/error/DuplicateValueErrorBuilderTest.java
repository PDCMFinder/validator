package org.pdxfinder.validator.tablevalidation.error;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error_creators.DuplicateValueErrorCreator;

public class DuplicateValueErrorBuilderTest {

  private DuplicateValueErrorCreator duplicateValueErrorCreator = new DuplicateValueErrorCreator();

  @Test
  public void message() {
    String expected = "Duplicates values found: [a]";
    ColumnReference uniqueColumn = ColumnReference.of("table", "column");
    Set<String> duplicateValues = new HashSet<>(Arrays.asList("a"));
    ValidationError error =
        duplicateValueErrorCreator.create(uniqueColumn, duplicateValues);
    assertEquals(expected, error.getColumnMessage());
  }
}
