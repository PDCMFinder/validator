package org.pdxfinder.validator.tablevalidation.dao;

import org.junit.Assert;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;

public class ColumnReferencesTests {

  @Test
  public void Given_charset_hasCharset_willReportSo() {
    var columnReference = new ColumnReference("table", "column");
    var charsetFreeText = Charsets.FREE_TEXT;
    columnReference.setCharset(charsetFreeText.getValueRestriction());
    Assert.assertTrue(columnReference.hasCharset(charsetFreeText));
  }

}
