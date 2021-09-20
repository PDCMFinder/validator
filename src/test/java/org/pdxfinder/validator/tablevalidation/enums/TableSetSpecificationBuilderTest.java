package org.pdxfinder.validator.tablevalidation.enums;

import org.junit.Assert;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.TableSetSpecificationBuilder;

public class TableSetSpecificationBuilderTest {

  @Test
  public void Given_yamlConfigFiles_When_generateIsCalled_DoNotReturnNull() {
    var tableSetSpecificationBuilder = new TableSetSpecificationBuilder("metadata");
    TableSetSpecification specification = tableSetSpecificationBuilder.generate();

    Assert.assertNotNull(specification);
  }

}
