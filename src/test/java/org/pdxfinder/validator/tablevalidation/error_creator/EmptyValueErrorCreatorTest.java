package org.pdxfinder.validator.tablevalidation.error_creator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.pdxfinder.validator.tableutilities.TableUtilities;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error_creators.MissingValueErrorCreator;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class EmptyValueErrorCreatorTest {

  private Map<String, Table> completeTableSet = new HashMap<>();
  private final String TABLE_1 = "table_1.tsv";
  private final String PROVIDER = "PROVIDER-BC";
  private MissingValueErrorCreator emptyValueErrorCreator = new MissingValueErrorCreator();

  private Set<String> minimalRequiredTable = Stream.of(TABLE_1).collect(Collectors.toSet());

  private Map<String, Table> makeCompleteTableSet() {
    Map<String, Table> completeFileSet = new HashMap<>();
    minimalRequiredTable.forEach(s -> completeFileSet.put(s, Table.create()));
    return completeFileSet;
  }

  private TableSetSpecification requireColumn =
      TableSetSpecification.create()
          .setProvider(PROVIDER)
          .addNonEmptyColumns(ColumnReference.of(TABLE_1, "required_col"));

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    completeTableSet = makeCompleteTableSet();
  }

  @Test
  public void checkAllNonEmptyValuesPresent_givenNoMissingValues_emptyErrorList() {
    Map<String, Table> fileSetWithValidTable = new HashMap<>();
    Table tableWithNoMissingValues =
        TableUtilities.fromString(TABLE_1, "required_col", "required_value");
    fileSetWithValidTable.put(TABLE_1, tableWithNoMissingValues);

    assertTrue(
        emptyValueErrorCreator.generateErrors(fileSetWithValidTable, requireColumn).isEmpty());
  }

  @Test
  public void checkAllNonEmptyValuesPresent_givenMissingValue_addsMissingValueErrorToErrorList() {
    Map<String, Table> fileSetWithInvalidTable = new HashMap<>();
    Table tableWithMissingValue =
        completeTableSet
            .get(TABLE_1)
            .addColumns(StringColumn.create("required_col", Collections.singletonList("")));
    ColumnReference requiredCol = ColumnReference.of(TABLE_1, "required_col");
    String missingValues = "[6]";
    fileSetWithInvalidTable.put(TABLE_1, tableWithMissingValue);
    List<ValidationError> expected =
        Collections.singletonList(
            emptyValueErrorCreator
                .create(requiredCol, missingValues));

    List<ValidationError> error = emptyValueErrorCreator.generateErrors(fileSetWithInvalidTable,
        requireColumn);
    var table_error = error.get(0);
    ValidationError expected_error = expected.get(0);
    assertEquals(expected_error.getColumnMessage(), table_error.getColumnMessage());
    assertEquals(expected_error.getTableName(), table_error.getTableName());
    assertEquals(expected_error.getType(), expected_error.getType());
  }

  @Test
  public void
      checkAllNonEmptyValuesPresent_givenMissingValueInRow2_addsMissingValueErrorToErrorList() {
    Map<String, Table> fileSetWithInvalidTable = new HashMap<>();
    Table tableWithMissingValue =
        completeTableSet
            .get(TABLE_1)
            .addColumns(
                StringColumn.create("required_col", Arrays.asList("value_1", "")),
                StringColumn.create("other_col", Arrays.asList("", "This is the invalid row")));
    fileSetWithInvalidTable.put(TABLE_1, tableWithMissingValue);
    ColumnReference requiredCol = ColumnReference.of(TABLE_1, "required_col");
    String missingRowNumbers = "[7]";
    List<ValidationError> expected =
        Collections.singletonList(emptyValueErrorCreator.create(requiredCol, missingRowNumbers));

    List<ValidationError> error = emptyValueErrorCreator.generateErrors(fileSetWithInvalidTable,
        requireColumn);
    ValidationError missing_error = error.get(0);
    ValidationError expected_error = expected.get(0);
    assertEquals(expected_error.getColumnMessage(), missing_error.getColumnMessage());
    assertEquals(expected_error.getTableName(), missing_error.getTableName());
    assertEquals(expected_error.getType(), expected_error.getType());
  }
}
