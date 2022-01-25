package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.errorCreators.MissingValueErrorCreator;

import static org.junit.Assert.assertEquals;

public class EmptyValueErrorBuilderTest {

  private MissingValueErrorCreator emptyValueErrorCreator = new MissingValueErrorCreator();

  @Test
  public void message_givenMissingValue_returnsAppropriateError() {
    String expected = "No value found";
    String expectedRowMessage = "[0]";
    ColumnReference columnReference = ColumnReference.of("table", "column");
    ValidationError error =
            emptyValueErrorCreator.create(columnReference, "[0]");
    assertEquals(expected, error.getCause());
    assertEquals(expectedRowMessage, error.getRow());
  }
}
