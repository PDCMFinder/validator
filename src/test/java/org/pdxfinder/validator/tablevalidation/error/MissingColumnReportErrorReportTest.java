package org.pdxfinder.validator.tablevalidation.error;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.ColumnReference;

public class MissingColumnReportErrorReportTest {

  private MissingColumnErrorCreator missingColumnErrorCreator = new MissingColumnErrorCreator();
  private String PROVIDER = "provider";

  @Test
  public void message_givenMissingValue_returnsAppropriateError() {
    String expected = "Error in [table] for provider [provider]: Missing column: [column]";

    ColumnReference columnReference = ColumnReference.of("table", "column");
    MissingColumnError error = missingColumnErrorCreator.create(columnReference, PROVIDER);

    assertEquals(expected, error.message());
  }

  @Test
  public void verboseMessage_sameAsMessage() {
    ColumnReference columnReference = ColumnReference.of("table", "column");
    MissingColumnError error = missingColumnErrorCreator.create(columnReference, PROVIDER);

    assertEquals(error.message(), error.verboseMessage());
  }
}
