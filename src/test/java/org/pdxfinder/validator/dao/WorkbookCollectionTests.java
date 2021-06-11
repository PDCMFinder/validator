package org.pdxfinder.validator.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Assert;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.pdxfinder.validator.tablevalidation.dao.WorkbookTable;

public class WorkbookCollectionTests {

  @Test
  public void Given_ymlFile_fromYamlIsCalled_returnWorkbookCollection() {
    PdxWorkbookCollection workbookCollection =
        PdxWorkbookCollection.fromYaml("data/WorkbookCollection.yml");
    Assert.assertEquals(1, workbookCollection.len());
  }

  @Test
  public void Given_ymlWithColumnReference_UsingMappper_ReturnDao() throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    String columnReferenceYaml =
        "name: patient_id\n"
            + "charset: url_safe\n"
            + "attributes:\n"
            + "  - essential\n"
            + "  - unique\n"
            + "relation: []";
    ColumnReference columnReference = mapper.readValue(columnReferenceYaml, ColumnReference.class);
    Assert.assertNotNull(columnReference);
  }

  @Test
  public void Given_ymlWithEmptyWorkbookTable_When_UsingMapper_ReturnDAO()
      throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    String WorkBookTableYaml =
        "table: \"patient\" \n"
            + "column_references: []";
    WorkbookTable workBookTable = mapper.readValue(WorkBookTableYaml, WorkbookTable.class);
    Assert.assertNotNull(workBookTable);
  }

  @Test
  public void Given_ymlWithEmptyWorkbook_When_UsingMapper_ReturnDAO()
      throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    String WorkBookYaml =
        "workbook_title: \"\"\n"
            + "workbook: []";
    Workbook workBook = mapper.readValue(WorkBookYaml, Workbook.class);
    Assert.assertNotNull(workBook);
  }

  @Test
  public void Given_ymlWithEmptyPdxWorkbookCollection_When_UsingMapper_ReturnDA0()
      throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    String pdxWorkbookCollectionYaml =
        "workbooks: []";
    PdxWorkbookCollection pdxWorkbookCollection = mapper
        .readValue(pdxWorkbookCollectionYaml, PdxWorkbookCollection.class);
    Assert.assertNotNull(pdxWorkbookCollection);
  }

  @Test
  public void Given_ymlWithAllDomains_When_UsingMapper_Return_ReturnDAO()
      throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());
    String filledWorkbookCollectionYaml =
        "workbooks:\n"
            + "- workbook_title: metadata\n"
            + "  workbook:\n"
            + "  - table: patient\n"
            + "    column_references: []";

    PdxWorkbookCollection pdxWorkbookCollection = mapper
        .readValue(filledWorkbookCollectionYaml, PdxWorkbookCollection.class);
    Assert.assertNotNull(pdxWorkbookCollection);

  }

}
