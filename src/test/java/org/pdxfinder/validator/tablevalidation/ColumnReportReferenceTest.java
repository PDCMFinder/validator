package org.pdxfinder.validator.tablevalidation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ColumnReportReferenceTest {

  @Test
  public void getters_givenInstantiation_returnsValues() {
    ColumnReference columnReference = ColumnReference.of("table", "column");
    assertEquals("table", columnReference.table());
    assertEquals("column", columnReference.column());
  }

  @Test
  public void factoryMethod_givenCalled_createsNewInstance() {
    ColumnReference columnReference = ColumnReference.of("table", "column");
    assertEquals("table", columnReference.table());
    assertEquals("column", columnReference.column());
  }

  @Test
  public void equals_givenIdenticalObjects_symmetricallyEqual() {
    ColumnReference x = ColumnReference.of("x", "x");
    ColumnReference y = ColumnReference.of("x", "x");
    assertTrue(x.equals(y) && y.equals(x));
  }

  @Test
  public void equals_givenIdenticalObjects_hashCodeIsEqual() {
    ColumnReference x = ColumnReference.of("x", "x");
    ColumnReference y = ColumnReference.of("x", "x");
    assertEquals(x.hashCode(), y.hashCode());
  }

  @Test
  public void equals_givenSameObject_returnsTrue() {
    ColumnReference x = ColumnReference.of("x", "x");
    assertEquals(x, x);
  }

  @Test
  public void equals_givenNonIdenticalObjects_returnsFalse() {
    ColumnReference x = ColumnReference.of("x", "x");
    ColumnReference y = ColumnReference.of("y", "y");
    assertNotEquals(x, y);
  }

  @Test
  public void hashCode_givenObjectPutInMap_identicalKeyRetrievesTheValue() {
    ColumnReference x = ColumnReference.of("x", "x");
    ColumnReference y = ColumnReference.of("x", "x");
    Map<ColumnReference, String> map = new HashMap<>();
    map.put(x, "this");
    assertEquals("this", map.get(y));
  }
}
