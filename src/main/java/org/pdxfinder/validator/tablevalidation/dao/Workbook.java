package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.pdxfinder.validator.tablevalidation.Relation;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;
import org.pdxfinder.validator.tablevalidation.enums.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"workbook_title", "workbook"})
public class Workbook {

  private static final Logger log = LoggerFactory.getLogger(Workbook.class);

  @JsonProperty("workbook_title")
  private String workbook_title;

  @JsonProperty("workbook")
  private List<WorkbookTable> workbookTables;

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
    return Arrays.stream(Charsets.values())
        .collect(Collectors.toMap(
            getAllColumnsWithCharsets(),
            Charsets::getValueRestriction
        ));
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
    return vt -> workbookTables.stream()
        .map(wb -> wb.getColumnsWithCharset(vt))
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  public Set<ColumnReference> matchingColumnFromTable(String tableName, String columnNamePatterns) {
    return getColumns().stream()
        .filter(c -> c.table().contains(tableName))
        .filter(c -> containsAny(c.column(), new String[]{columnNamePatterns}))
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  private Set<ColumnReference> getColumns() {
    return workbookTables.stream()
        .map(WorkbookTable::getColumnReferences)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private boolean containsAny(String inputStr, String[] patterns) {
    return Arrays.stream(patterns).parallel().anyMatch(inputStr::equalsIgnoreCase);
  }

  public Set<WorkbookTable> getAllTablesColumns() {
    return new HashSet<>(workbookTables);
  }

  public String getWorkbookTitle() {
    return workbook_title;
  }

  public void setWorkbook_title(String workbook_title) {
    this.workbook_title = workbook_title;
  }

  public List<WorkbookTable> getWorkbookTables() {
    return workbookTables;
  }

  public void setWorkbookTables(List<WorkbookTable> workbookTables) {
    this.workbookTables = workbookTables;
  }
}
