package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.ErrorType;
import org.pdxfinder.validator.tablevalidation.errorCreators.MissingColumnErrorCreator;

import static org.junit.Assert.assertEquals;

public class MissingColumnErrorBuilderTest {

  private MissingColumnErrorCreator missingColumnErrorCreator = new MissingColumnErrorCreator();

  @Test
  public void message_givenMissingValue_returnsAppropriateError() {
    String expected = "Required column not found in table";
    String expected_table = "table1";
    ColumnReference columnReference = ColumnReference.of(expected_table, "column");
    ValidationError error = missingColumnErrorCreator.create(columnReference);
    assertEquals(expected, error.getCause());
    assertEquals(expected_table, error.getTableName());
    assertEquals(ErrorType.MISSING_COLUMN.getErrorType(), error.getErrorType());
  }
}

