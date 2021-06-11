package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.pdxfinder.validator.tablevalidation.Relation;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"table", "column_references"})
public class WorkbookTable {

  private String table;
  private List<ColumnReference> columnReferences;

  public WorkbookTable() {
  }

  @JsonProperty("column_"
      + "references")
  public void setColumnReference(List<ColumnReference> columnReference) {
    this.columnReferences = columnReferences;
  }

  public List<ColumnReference> getColumnReferences() {
    return columnReferences;
  }

  @JsonIgnore
  public Set<ColumnReference> getColumnsWithAttribute(Rules rule) {
    return columnReferences.stream().filter(c -> c.hasAttribute(rule)).collect(Collectors.toSet());
  }

  @JsonIgnore
  public Map<Set<ColumnReference>, ValueRestrictions> getColumnsByCategories() {
    return columnReferences.stream()
        .filter(ColumnReference::hasCategories)
        .collect(Collectors.toMap(
            Set::of,
            ColumnReference::getCategories
        ));
  }

  @JsonIgnore
  public Set<ColumnReference> getColumnsWithCharset(Charsets charset) {
    return columnReferences.stream()
        .filter(cr -> cr.hasCharset(charset))
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  public Set<Relation> getRelationsFromColumns() {
    return columnReferences.stream()
        .map(ColumnReference::getRelation)
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  private void applyTableName() {
    columnReferences.forEach(x -> x.setTableName(table));
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }
}
