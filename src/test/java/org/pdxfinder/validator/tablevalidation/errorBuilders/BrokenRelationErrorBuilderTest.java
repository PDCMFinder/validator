package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.junit.Ignore;
import org.pdxfinder.validator.tablevalidation.RelationTestUtilities;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import org.pdxfinder.validator.tablevalidation.errorCreators.BrokenRelationErrorCreator;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BrokenRelationErrorBuilderTest {

  private BrokenRelationErrorCreator brokenTableRelationErrorCreator =
      new BrokenRelationErrorCreator();

  @Ignore
  public void toString_givenBrokenRelationMissingRightColumn_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [TEST]: Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]"
            + ": because [bar.tsv] is missing column [foo_id]:\n"
            + " not_foo_id  |\n"
            + "--------------";
    Relation relation =
        RelationTestUtilities.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));

    var error =
            brokenTableRelationErrorCreator.create(
                    "bar.tsv",
                    "left_column",
                    relation,
                    "because [bar.tsv] is missing column [foo_id]");

    assertEquals(expected, error.toString());
  }

  @Ignore
  public void verboseMessage_givenBrokenRelationOrphanIdsInRightColumn_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]: "
            + "2 orphan row(s) found in [bar.tsv]:\n"
            + " foo_id  |\n"
            + "----------\n"
            + "      1  |\n"
            + "      1  |";
    Relation relation =
        RelationTestUtilities.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));
    Table tableMissingValues =
            Table.create().addColumns(StringColumn.create("foo_id", Arrays.asList("1", "1")));
    var error =
            brokenTableRelationErrorCreator.create(
                    "bar.tsv",
                    "left_column",
                    relation,
                    "2 orphan row(s) found in [bar.tsv]");

    assertEquals(expected, error);
  }

  @Ignore
  public void message_givenError_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]: "
            + "2 orphan row(s) found in [bar.tsv]";
    Relation relation =
        RelationTestUtilities.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));
    Table tableMissingValues =
            Table.create().addColumns(StringColumn.create("foo_id", Arrays.asList("1", "1")));
    var error =
            brokenTableRelationErrorCreator.create(
                    "bar.tsv",
                    "left_column",
                    relation,
                    "2 orphan row(s) found in [bar.tsv]");
    assertEquals(expected, error);
  }
}
