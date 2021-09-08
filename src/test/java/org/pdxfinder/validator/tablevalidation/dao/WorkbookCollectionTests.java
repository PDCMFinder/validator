package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Assert;
import org.junit.Test;

public class WorkbookCollectionTests {

  @Test
  public void Given_ymlFile_fromYamlIsCalled_returnWorkbookCollection() {
    PdxWorkbookCollection workbookCollection =
        PdxWorkbookCollection.fromYaml("data/WorkbookCollection.yml");
    Assert.assertTrue(0 < workbookCollection.len());
  }

  @Test
  public void Given_completeMetadataYML_FromGeneratedPdxWorkbookCollection_testForNullPointers() {
    PdxWorkbookCollection workbookCollection =
        PdxWorkbookCollection.fromYaml("data/WorkbookCollection.yml");
    var metadataWorkbook = workbookCollection.getWorkbooks().get(0);
    Assert.assertNotNull(metadataWorkbook.getTableNames());
    Assert.assertNotNull(metadataWorkbook.getWorkbookTitle());
    Assert.assertNotNull(metadataWorkbook.getAllColumnRelations());
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
    var workbook = getWorkbook();
    Assert.assertNotNull(workbook);
  }
}
