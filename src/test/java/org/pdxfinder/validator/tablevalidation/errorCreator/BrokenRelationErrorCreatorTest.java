package org.pdxfinder.validator.tablevalidation.errorCreator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.RelationTestUtilities;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;
import org.pdxfinder.validator.tablevalidation.errorCreators.BrokenRelationErrorCreator;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BrokenRelationErrorCreatorTest {

  private BrokenRelationErrorCreator brokenInterTableRelationErrorCreator =
          new BrokenRelationErrorCreator();

  private final String LEFT_TABLE = "left_table.tsv";
  private final String RIGHT_TABLE = "right_table.tsv";
  private final Relation INTER_TABLE_RELATION =
          RelationTestUtilities.betweenTableKeys(
          ColumnReference.of(LEFT_TABLE, "id"), ColumnReference.of(RIGHT_TABLE, "table_1_id"));

  private final Relation INTRA_TABLE_ONE_TO_MANY =
      RelationTestUtilities.betweenTableColumns(
              RelationType.TABLE_KEY_MANY_TO_ONE,
              ColumnReference.of(LEFT_TABLE, "id"),
              ColumnReference.of(LEFT_TABLE, "table_1_id"));

  private final Relation INTRA_TABLE_ONE_TO_ONE =
      RelationTestUtilities.betweenTableColumns(
          RelationType.ONE_TO_ONE,
          ColumnReference.of(LEFT_TABLE, "id"),
          ColumnReference.of(LEFT_TABLE, "table_1_id"));

  private final String PROVIDER = "PROVIDER-BC";

  private Map<String, Table> makeTableSetWithSimpleJoin() {
    Table leftTable =
        Table.create(LEFT_TABLE)
            .addColumns(StringColumn.create("id", Collections.singletonList("1")));
    Table rightTable =
        Table.create(RIGHT_TABLE)
            .addColumns(StringColumn.create("table_1_id", Collections.singletonList("1")));
    Map<String, Table> tableSetWithSimpleJoin = new HashMap<>();
    tableSetWithSimpleJoin.put(LEFT_TABLE, leftTable);
    tableSetWithSimpleJoin.put(RIGHT_TABLE, rightTable);
    return tableSetWithSimpleJoin;
  }

  private final TableSetSpecification SIMPLE_JOIN_SPECIFICATION =
      TableSetSpecification.create().setProvider(PROVIDER).addRelations(INTER_TABLE_RELATION);

  private final TableSetSpecification ONE_TO_MANY_SPECIFICATION =
      TableSetSpecification.create().setProvider(PROVIDER).addRelations(INTRA_TABLE_ONE_TO_MANY);

  private final TableSetSpecification ONE_TO_ONE_SPECIFICATION =
      TableSetSpecification.create().setProvider(PROVIDER).addRelations(INTRA_TABLE_ONE_TO_ONE);

  @Test
  public void oneToManyNoError_givenValidOneToManyJoin_emptyErrorList() {
    Table leftTable =
        Table.create(LEFT_TABLE)
            .addColumns(
                StringColumn.create("id", Arrays.asList("1", "2", "3")),
                StringColumn.create("table_1_id", Arrays.asList("1", "1", "1")));
    Map<String, Table> tableSetWithOneToMany = new HashMap<>();
    tableSetWithOneToMany.put(LEFT_TABLE, leftTable);
    assertThat(
        brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithOneToMany, ONE_TO_MANY_SPECIFICATION)
            .isEmpty(),
        is(true));
  }

  @Test
  public void oneToManyError_givenInvalidValidOneToManyJoin_hasErrorEntry() {
    Table leftTable =
            Table.create(LEFT_TABLE)
                    .addColumns(
                            StringColumn.create("id", Arrays.asList("1", "1", "1")),
                            StringColumn.create("table_1_id", Arrays.asList("1", "2", "3")));
    Map<String, Table> tableSetWithOneToManyWithErrors = new HashMap<>();
    tableSetWithOneToManyWithErrors.put(LEFT_TABLE, leftTable);
    var brokenTableErrors = brokenInterTableRelationErrorCreator.generateErrors(tableSetWithOneToManyWithErrors, ONE_TO_MANY_SPECIFICATION);
    assertEquals(brokenTableErrors.size(), 1);
  }

  @Test
  public void oneToOne_givenValidPairOfColumns_hasNoErrorEntry() {
    Table leftTable =
        Table.create(LEFT_TABLE)
            .addColumns(
                StringColumn.create("id", Arrays.asList("1", "2", "3")),
                StringColumn.create("table_1_id", Arrays.asList("3", "2", "1")));
    Map<String, Table> tableSetWithOneToOne = new HashMap<>();
    tableSetWithOneToOne.put(LEFT_TABLE, leftTable);
    assertThat(
        brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithOneToOne, ONE_TO_MANY_SPECIFICATION)
            .isEmpty(),
        is(true));
  }

  @Test
  public void oneToOneErro_givenInvalidPairOfColumns_hasErrorEntry() {
    Table leftTable =
        Table.create(LEFT_TABLE)
            .addColumns(
                StringColumn.create("id", Arrays.asList("1", "1", "2", "2", "5", "6", "7", "8")),
                StringColumn.create(
                    "table_1_id", Arrays.asList("1", "2", "3", "4", "5", "5", "7", "8")));
    Map<String, Table> tableSetWithOneToOne = new HashMap<>();
    tableSetWithOneToOne.put(LEFT_TABLE, leftTable);
    assertThat(
        brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithOneToOne, ONE_TO_ONE_SPECIFICATION)
            .isEmpty(),
        is(false));
  }

  @Test
  public void checkRelationsValid_givenMissingValueInRightColumn_ErrorListWithOrphanLeftRows() {
    Map<String, Table> tableSetWithSimpleJoin = makeTableSetWithSimpleJoin();
    tableSetWithSimpleJoin
            .get(LEFT_TABLE)
            .replaceColumn(StringColumn.create("id", Collections.EMPTY_LIST));
    ValidationError expected =
            brokenInterTableRelationErrorCreator
                    .create(
                            LEFT_TABLE,
                            "id",
                            INTER_TABLE_RELATION,
                            String.format("1 values in column table_1_id of the %s table are not found in this column: 1", RIGHT_TABLE)
                    );
    var brokenInterTableRelationErrors = brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithSimpleJoin, SIMPLE_JOIN_SPECIFICATION);
    assertEquals(1, brokenInterTableRelationErrors.size());
    assertTrue(brokenInterTableRelationErrors.get(0).equals(expected));
  }

  @Test
  public void checkRelationsValid_givenMissingValuesInLeftColumn_ErrorListWithOrphanRightRows() {
    Map<String, Table> tableSetWithSimpleJoin = makeTableSetWithSimpleJoin();
    tableSetWithSimpleJoin
            .get(LEFT_TABLE)
            .replaceColumn(StringColumn.create("id", Collections.EMPTY_LIST));
    ValidationError expected =
            brokenInterTableRelationErrorCreator
                    .create(
                            LEFT_TABLE,
                            "id",
                            INTER_TABLE_RELATION,
                            String.format("1 values in column table_1_id of the %s table are not found in this column: 1", RIGHT_TABLE)
                    );
    var brokenIntertableRelationErrors = brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithSimpleJoin, SIMPLE_JOIN_SPECIFICATION);
    assertEquals(1, brokenIntertableRelationErrors.size());
    assertTrue(brokenIntertableRelationErrors.get(0).equals(expected));
  }

  @Test
  public void
      checkRelationsValid_givenMissingValueInLeftAndRightColumn_ErrorListWithMissingValueRows() {
    Map<String, Table> tableSetWithSimpleJoin = makeTableSetWithSimpleJoin();
    tableSetWithSimpleJoin
            .get(LEFT_TABLE)
            .replaceColumn(StringColumn.create("id", Arrays.asList("not 1", "not 1")));
    List<ValidationError> expected =
            Arrays.asList(
                    brokenInterTableRelationErrorCreator
                            .create(
                                    LEFT_TABLE,
                                    "id",
                                    INTER_TABLE_RELATION,
                                    String.format("1 values in column table_1_id of the %s table are not found in this column: 1", RIGHT_TABLE)
                            ));
    var brokenInterTableRelationError = brokenInterTableRelationErrorCreator
            .generateErrors(tableSetWithSimpleJoin, SIMPLE_JOIN_SPECIFICATION);
    assertEquals(new HashSet<>(brokenInterTableRelationError), new HashSet<>(expected));
  }

  @Test
  public void checkToString_GivenIdenticalPairs_hashesMatch() {
    List<String> columnValuesLeft = Arrays.asList("value1", "value2", "value3");
    List<String> columnValuesRight = Arrays.asList("value4", "value5", "value6");
    StringColumn column1 = StringColumn.create("column1", columnValuesLeft);
    StringColumn column2 = StringColumn.create("column2", columnValuesRight);
    Pair<StringColumn, StringColumn> pair1 = Pair.of(column1, column2);
    Pair<StringColumn, StringColumn> pair2 = Pair.of(column1, column2);
    assertEquals(pair1, pair2);
    assertEquals(pair1.toString(), pair2.toString());
  }
}
