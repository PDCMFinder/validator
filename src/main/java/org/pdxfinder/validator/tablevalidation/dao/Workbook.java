package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"workbook_title", "workbook"})
public class Workbook {

  @JsonProperty("workbook_title")
  private String workbookTitle;

  @JsonProperty("workbook")
  private List<WorkbookTable> workbookTables;

  public void init() {
    workbookTables.forEach(WorkbookTable::init);
  }

  public Set<String> getTableNames() {
    return workbookTables.stream().map(WorkbookTable::getTable).collect(Collectors.toSet());
  }

  @JsonIgnore
  public Set<ColumnReference> getAllColumnsWithAttribute(Rules rule) {
    return workbookTables.stream()
        .map(wb -> wb.getColumnsWithAttribute(rule))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  public Map<Set<ColumnReference>, ValueRestrictions> getColumnsByCharset() {
    Map<Set<ColumnReference>, ValueRestrictions> charsetColumnMap = new HashMap<>();
    for (Charsets charset : Charsets.values()) {
      Set<ColumnReference> columnsWithCharsets = getAllColumnsWithCharsets().apply(charset);
      if (!columnsWithCharsets.isEmpty()) {
        charsetColumnMap.put(
            columnsWithCharsets,
            charset.getValueRestriction()
        );
      }
    }
    return charsetColumnMap;
  }

  @JsonIgnore
  public Map<Set<ColumnReference>, ValueRestrictions> getAllColumnsByCategories() {
    return workbookTables.stream()
        .map(WorkbookTable::getColumnsByCategories)
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue)
        );
  }

  @JsonIgnore
  public Set<Relation> getAllColumnRelations() {
    return workbookTables.stream()
        .map(WorkbookTable::getRelationsFromColumns)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  private Function<Charsets, Set<ColumnReference>> getAllColumnsWithCharsets() {
    return vt ->
        workbookTables.stream()
            .map(wb -> wb.getColumnsWithCharset(vt))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
  }

  @JsonIgnore
  public Set<ColumnReference> matchingColumnFromTable(String tableName, String columnNamePatterns) {
    return getAllTableColumns().stream()
        .filter(c -> c.table().contains(tableName))
        .filter(c -> containsAny(c.column(), new String[]{columnNamePatterns}))
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  public Set<ColumnReference> getAllTableColumns() {
    return workbookTables.stream()
        .map(WorkbookTable::getColumnReferences)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private boolean containsAny(String inputStr, String[] patterns) {
    return Arrays.stream(patterns).parallel().anyMatch(inputStr::equalsIgnoreCase);
  }

  public Set<WorkbookTable> getAllTables() {
    return new HashSet<>(workbookTables);
  }

  public String getWorkbookTitle() {
    return workbookTitle;
  }

  public void setWorkbookTitle(String workbookTitle) {
    this.workbookTitle = workbookTitle;
  }

  public List<WorkbookTable> getWorkbookTables() {
    return workbookTables;
  }

  public void setWorkbookTables(List<WorkbookTable> workbookTables) {
    this.workbookTables = workbookTables;
  }
}
