package org.pdxfinder.validator.tablevalidation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class ValidationServiceTest {

  private final String TABLE_1 = "table_1.tsv";
  private Set<String> minimalRequiredTable = Stream.of(TABLE_1).collect(Collectors.toSet());

  private Map<String, Table> makeCompleteTableSet() {
    Map<String, Table> completeFileSet = new HashMap<>();
    minimalRequiredTable.forEach(
        s -> completeFileSet.put(s, Table.create(s, StringColumn.create("valid_col"))));
    return completeFileSet;
  }

  private Map<String, Table> tableSet = makeCompleteTableSet();
  TableSetSpecification tableSetSpecification =
      TableSetSpecification.create()
          .setProvider("PROVIDER-BC")
          .addRequiredColumns(ColumnReference.of(TABLE_1, "valid_col"));


  @Mock
  private ValidationService validationService;

  @Before
  public void setUp() {
    this.validationService = new ValidationService();
  }

  @Ignore
  public void validate_givenNoValidation_producesEmptyErrorList() {
    assertThat(validationService.getValidationErrors().isEmpty(), is(true));
  }

  @Ignore
  public void validate_givenNoMissingTables_checksForMissingTablesAndNoErrorsFound() {
    validationService.validate(tableSet, tableSetSpecification);
  }
}
