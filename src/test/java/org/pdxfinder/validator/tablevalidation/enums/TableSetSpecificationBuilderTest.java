package org.pdxfinder.validator.tablevalidation.enums;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.enums.CharsetRestrictions.Yml;

public class TableSetSpecificationBuilderTest {

  @Test
  public void Given_yamlConfigFiles_When_generateIsCalled_DoNotReturnNull() {
    PdxWorkbookCollection workbook = PdxWorkbookCollection
        .fromYaml(Yml.WORKBOOK_COLLECTION.Location());

    System.out.print(workbook.toString());

    //Assert.assertNotNull(workbook.getWorkbookTitle());
    //Assert.assertNotNull(workbook.getWorkbookTables());
  }

}
