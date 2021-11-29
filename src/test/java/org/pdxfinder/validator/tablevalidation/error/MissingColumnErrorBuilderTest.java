package org.pdxfinder.validator.tablevalidation.error;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;
import org.pdxfinder.validator.tablevalidation.error_creators.MissingColumnErrorCreator;

import static org.junit.Assert.assertEquals;

public class MissingColumnErrorBuilderTest {

  private MissingColumnErrorCreator missingColumnErrorCreator = new MissingColumnErrorCreator();

  @Test
  public void message_givenMissingValue_returnsAppropriateError() {
    String expected = "Missing column: [column]";
    String expected_table = "table1";
    ColumnReference columnReference = ColumnReference.of(expected_table, "column");
    ValidationError error = missingColumnErrorCreator.create(columnReference);
    assertEquals(expected, error.getCause());
    assertEquals(expected_table, error.getTableName());
    assertEquals(ErrorType.MISSING_COLUMN.getErrorType(), error.getErrorType());
  }
}

