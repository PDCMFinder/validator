package org.pdxfinder.validator.tablevalidation.error;

import org.junit.Ignore;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error_creators.IllegalValueErrorCreator;

import static org.junit.Assert.assertEquals;

public class IllegalValueErrorBuilderTest {

  private IllegalValueErrorCreator illegalValueErrorCreator = new IllegalValueErrorCreator();


  @Ignore
  public void columnMissing_givenIllegalValueError_returnsAppropriateMessage() {
    String expected =
            "Error in [bar.tsv] for provider [TEST]: because [bar.tsv] is missing column [foo_id]:\n"
                    + " not_foo_id  |\n"
                    + "--------------";
    ValidationError error =
            illegalValueErrorCreator.create(
                    "bar.tsv",
                    10, "because [bar.tsv] is missing column [foo_id]",
                    "invalidValue", "food_id");
    assertEquals(expected, error.getCause());
    assertEquals(expected, error.getRule());
  }

  @Ignore
  public void verboseMessage_givenInvalidCharactersInColumn_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "in column [foo_id] found 1 values "
            + "has characters not contained in US ASCII Alphabet and ._~- : TE#/ST:\n"
            + " foo_id  |\n"
            + "----------\n"
            + " TE#/ST  |";
    ValidationError error =
        illegalValueErrorCreator.create(
            "bar.tsv",
            10,
            "in column [foo_id] found 1 values has characters not contained in US ASCII Alphabet and ._~- : TE#/ST",
            "invalidValues", "foo_id");
    assertEquals(expected, error);
  }

  @Ignore
  public void errorMessage_givenInvalidCharactersInColumn_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "in column [foo_id] found 1 values has characters not contained in US ASCII Alphabet and ._~- : TE#/ST";
    ValidationError error =
        illegalValueErrorCreator.create(
            "bar.tsv",
            10,
            "in column [foo_id] found 1 values has characters not contained in US ASCII Alphabet and ._~- : TE#/ST",
            "invalidValues", "foo_id");
    assertEquals(expected, error.getRule());
  }
}
