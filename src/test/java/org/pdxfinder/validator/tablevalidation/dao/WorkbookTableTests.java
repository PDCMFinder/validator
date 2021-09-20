package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.Arrays;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;
import org.pdxfinder.validator.tablevalidation.enums.RelationType;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

public class WorkbookTableTests {

  private WorkbookTable getWorkbookTableWithCategories() throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    String workbookTableYaml =
        "table: patient\n"
            + "column_references:\n"
            + "  - name: sex\n"
            + "    attributes:\n"
            + "      - not_empty\n"
            + "      - unique\n"
            + "    categories:\n"
            + "      - male\n"
            + "      - female\n"
            + "      - not collected\n"
            + "      - not provided\n"
            + "    relation:\n"
            + "      - type: TABLE_KEY\n"
            + "        right_table: sample\n"
            + "        right_column: model_id\n";
    return mapper.readValue(workbookTableYaml, WorkbookTable.class);
  }

  private WorkbookTable getWorkbookTableWithCharset() throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    String workbookTableYaml =
        "table: sample\n"
            + "column_references:\n"
            + "  - name: patient_id\n"
            + "    attributes:\n"
            + "      - not_empty\n"
            + "    charset: free_text\n"
            + "    relation:\n"
            + "      - type: TABLE_KEY\n"
            + "        right_table: patient\n"
            + "        right_column: patient_id\n";
    return mapper.readValue(workbookTableYaml, WorkbookTable.class);
  }

  private Workbook getWorkbook() throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    String WorkBookYaml =
        "workbook_title: metadata\n"
            + "workbook: []";
    return mapper.readValue(WorkBookYaml, Workbook.class);
  }

  @Test
  public void Given_ymlWithColumnReferenceForCategorical_ColumReferenceMethodsWorkAppropriately()
      throws JsonProcessingException {
    WorkbookTable workbookTable = getWorkbookTableWithCategories();
    var columnReference = workbookTable.getColumnReferences().get(0);
    Assert.assertNotNull(columnReference);
    Assert.assertNotNull(columnReference.getCategories().getPredicate());
    Assert.assertEquals("sex", columnReference.column());
    Assert.assertEquals(2, columnReference.getAttributes().size());
    var relation = columnReference.getRelation().get(0);
    Assert.assertEquals("model_id", relation.rightColumn());
    Assert.assertEquals("sample", relation.rightColumnReference().table());
    Assert.assertEquals(RelationType.TABLE_KEY, relation.getValidity());
  }

  @Test
  public void Given_ymlWithColumnReferenceWithCharset_getCharset() throws JsonProcessingException {
    WorkbookTable workbookTable = getWorkbookTableWithCharset();
    Assert.assertEquals(1, workbookTable.getColumnsWithCharset(Charsets.FREE_TEXT).size());
  }


  @Test
  public void Given_ymlWithWorkbookTable_testWorkbookTableMethods() throws JsonProcessingException {
    var workbookTable = getWorkbookTableWithCategories();
    Assert.assertEquals("patient", workbookTable.getTable());
    Assert.assertEquals(1, workbookTable.getColumnReferences().size());
    Assert.assertEquals(1, workbookTable.getRelationsFromColumns().size());
    Assert.assertEquals(1, workbookTable.getColumnsWithAttribute(Rules.NOT_EMPTY).size());
    Assert.assertEquals(1, workbookTable.getColumnsWithAttribute(Rules.UNIQUE).size());
    var columnsByCategories = workbookTable.getColumnsByCategories();
    var predicate = columnsByCategories
        .values()
        .stream()
        .findFirst()
        .get()
        .getPredicate();
    Assert.assertFalse(predicate.test("male"));
    Assert.assertFalse(predicate.test("female"));
    Assert.assertFalse(predicate.test("not provided"));
    Assert.assertFalse(predicate.test("not collected"));
    Assert.assertTrue(predicate.test("fail"));

  }

  @Test
  public void Given_workbookWithFullset_when_mappedTestMethods_Return_appropriateDataStructures()
      throws JsonProcessingException {
    var workbook = getWorkbook();
    var workbookTableWithCategories = getWorkbookTableWithCategories();
    var workbookTableWithCharset = getWorkbookTableWithCharset();
    workbook
        .setWorkbookTables(Arrays.asList(workbookTableWithCategories, workbookTableWithCharset));
    Assert.assertEquals("metadata", workbook.getWorkbookTitle());
    Assert.assertEquals(2, workbook.getWorkbookTables().size());
    Assert.assertEquals(2, workbook.getAllColumnsWithAttribute(Rules.NOT_EMPTY).size());
    Assert.assertEquals(1, workbook.getAllColumnsWithAttribute(Rules.UNIQUE).size());
    Assert.assertEquals(2, workbook.getAllColumnRelations().size());
    Assert.assertTrue(workbook.getTableNames().contains("patient"));
    Assert.assertTrue(workbook.getTableNames().contains("sample"));
    Assert.assertEquals(2, workbook.getAllTables().size());
  }

  @Test
  public void Given_workbookWithFullset_when_mappedTestMethods_Return_ColumnNamesAreNotNull()
      throws JsonProcessingException {
    var workbook = getWorkbook();
    var workbookTableWithCategories = getWorkbookTableWithCategories();
    var workbookTableWithCharset = getWorkbookTableWithCharset();
    workbook
        .setWorkbookTables(Arrays.asList(workbookTableWithCategories, workbookTableWithCharset));
    Consumer<ColumnReference> testIfTableNameIsNull = x -> Assert.assertNotNull(x.table());
    Consumer<ColumnReference> testIfColumnNameIsNull = x -> Assert.assertNotNull(x.column());
    workbook.getAllTableColumns().forEach(testIfTableNameIsNull);
    workbook.getAllTableColumns().forEach(testIfColumnNameIsNull);
  }

  @Test
  public void Given_workbookWithFullset_when_mappedTestMethods_Return_CharsetPredicateWorks()
      throws JsonProcessingException {
    var workbook = getWorkbook();
    var workbookTableWithCategories = getWorkbookTableWithCategories();
    var workbookTableWithCharset = getWorkbookTableWithCharset();
    workbook
        .setWorkbookTables(Arrays.asList(workbookTableWithCategories, workbookTableWithCharset));
    var columnsByCharset = workbook.getColumnsByCharset();
    var predicate = columnsByCharset
        .values()
        .stream()
        .findFirst()
        .get()
        .getPredicate();
    Assert.assertTrue(predicate.test("\"\"\""));
  }


}
