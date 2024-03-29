package org.pdxfinder.validator.tablevalidation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.Relation;
import tech.tablesaw.api.Table;

public class TableSetSpecification {
  private String tablesetSpecificationName;
  private Set<String> requiredTables;
  private Set<ColumnReference> requiredColumns;
  private Set<ColumnReference> nonEmptyColumns;
  private Set<ColumnReference> uniqueColumns;
  private Map<Set<ColumnReference>, ValueRestrictions> valueRestrictedColumns;
  private Set<Relation> relations;
  private String provider = "Not Specified";

  private TableSetSpecification() {
    this.requiredTables = new HashSet<>();
    this.requiredColumns = new HashSet<>();
    this.nonEmptyColumns = new HashSet<>();
    this.uniqueColumns = new HashSet<>();
    this.valueRestrictedColumns = new HashMap<>();
    this.relations = new HashSet<>();
  }

  public static TableSetSpecification create() {
    return new TableSetSpecification();
  }

  public TableSetSpecification addRelations(Relation relation) {
    this.relations.add(relation);
    return this;
  }

  public TableSetSpecification addRelations(Set<Relation> relations) {
    this.relations.addAll(relations);
    return this;
  }

  public TableSetSpecification addRequiredTables(Set<String> requiredTables) {
    this.requiredTables.addAll(requiredTables);
    return this;
  }

  public TableSetSpecification addRequiredColumns(ColumnReference tableColumn) {
    this.requiredColumns.add(tableColumn);
    return this;
  }

  public TableSetSpecification addRequiredColumns(Set<ColumnReference> tableColumn) {
    this.requiredColumns.addAll(tableColumn);
    return this;
  }

  public TableSetSpecification addNonEmptyColumns(ColumnReference tableColumn) {
    this.nonEmptyColumns.add(tableColumn);
    return this;
  }

  public TableSetSpecification addNonEmptyColumns(Set<ColumnReference> tableColumns) {
    this.nonEmptyColumns.addAll(tableColumns);
    return this;
  }

  public TableSetSpecification addUniqueColumns(ColumnReference columnReference) {
    this.uniqueColumns.add(columnReference);
    return this;
  }

  public TableSetSpecification addUniqueColumns(Set<ColumnReference> columnReferences) {
    this.uniqueColumns.addAll(columnReferences);
    return this;
  }

  public TableSetSpecification addValueRestriction(
      Set<ColumnReference> columnReferences, ValueRestrictions valueRestrictions) {
    this.valueRestrictedColumns.put(columnReferences, valueRestrictions);
    return this;
  }

  public TableSetSpecification addAllValueRestrictions(
      Map<Set<ColumnReference>, ValueRestrictions> allvalues) {
    this.valueRestrictedColumns.putAll(allvalues);
    return this;
  }

  public String getProvider() {
    return provider;
  }

  public TableSetSpecification setProvider(String provider) {
    this.provider = provider;
    return this;
  }

  public TableSetSpecification setTablesetSpecificationName(String tablesetSpecificationName) {
    this.tablesetSpecificationName = tablesetSpecificationName;
    return this;
  }

  public Set<ColumnReference> getRequiredNonEmptyColumns() {
    return this.nonEmptyColumns;
  }

  public Set<ColumnReference> getUniqueColumns() {
    return this.uniqueColumns;
  }

  public Set<String> getRequiredTables() {
    return this.requiredTables;
  }

  public Set<ColumnReference> getRequiredColumns() {
    return this.requiredColumns;
  }

  public String getTablesetSpecificationName() {
    return tablesetSpecificationName;
  }

  public boolean hasRequiredColumns() {
    return getRequiredColumns() != null;
  }

  public Set<Relation> getRelations() {
    return this.relations;
  }

  public Map<Set<ColumnReference>, ValueRestrictions> getCharSetRestrictions() {
    return this.valueRestrictedColumns;
  }

  public Set<String> getMissingTablesFrom(Map<String, Table> fileList) {
    Set<String> missingFiles = requiredTables;
    missingFiles.removeAll(fileList.keySet());
    return missingFiles;
  }

  public static TableSetSpecification merge(TableSetSpecification... tableSetSpecifications) {
    TableSetSpecification mergedTableSetSpecifications = TableSetSpecification.create();
    for (TableSetSpecification tss : tableSetSpecifications) {
      mergedTableSetSpecifications.setProvider(tss.getProvider());
      mergedTableSetSpecifications.addRequiredTables(tss.getRequiredTables());
      mergedTableSetSpecifications.addRequiredColumns(tss.getRequiredColumns());
      mergedTableSetSpecifications.addNonEmptyColumns(tss.getRequiredNonEmptyColumns());
      mergedTableSetSpecifications.addUniqueColumns(tss.getUniqueColumns());
      mergedTableSetSpecifications.addRelations(tss.getRelations());
      mergedTableSetSpecifications.addAllValueRestrictions(tss.getCharSetRestrictions());
    }
    return mergedTableSetSpecifications;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TableSetSpecification that = (TableSetSpecification) o;

    if (getRequiredTables() != null
        ? !getRequiredTables().equals(that.getRequiredTables())
        : that.getRequiredTables() != null) {
      return false;
    }
    if (getRequiredColumns() != null
        ? !getRequiredColumns().equals(that.getRequiredColumns())
        : that.getRequiredColumns() != null) {
      return false;
    }
    if (getRequiredNonEmptyColumns() != null
        ? !getRequiredNonEmptyColumns().equals(that.getRequiredNonEmptyColumns())
        : that.getRequiredNonEmptyColumns() != null) {
      return false;
    }
    if (getUniqueColumns() != null
        ? !getUniqueColumns().equals(that.getUniqueColumns())
        : that.getUniqueColumns() != null) {
      return false;
    }
    if (getRelations() != null
        ? !getRelations().equals(that.getRelations())
        : that.getRelations() != null) {
      return false;
    }
    return getProvider().equals(that.getProvider());
  }

  @Override
  public int hashCode() {
    int result = getRequiredTables() != null ? getRequiredTables().hashCode() : 0;
    result = 31 * result + (getRequiredColumns() != null ? getRequiredColumns().hashCode() : 0);
    result =
        31 * result
            + (getRequiredNonEmptyColumns() != null ? getRequiredNonEmptyColumns().hashCode() : 0);
    result = 31 * result + (getUniqueColumns() != null ? getUniqueColumns().hashCode() : 0);
    result = 31 * result + (getRelations() != null ? getRelations().hashCode() : 0);
    result = 31 * result + getProvider().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner("\n ", TableSetSpecification.class.getSimpleName() + "[\n", "]")
        .add("requiredTables=\n\t" + String.join("\n\t", requiredTables))
        .add(
            "requiredColumns=\n\t"
                + String.join(
                "\n\t",
                requiredColumns.stream()
                    .map(ColumnReference::toString)
                    .collect(Collectors.toSet())))
        .add(
            "nonEmptyColumns=\n\t"
                + String.join(
                "\n\t",
                nonEmptyColumns.stream()
                    .map(ColumnReference::toString)
                    .collect(Collectors.toSet())))
        .add(
            "uniqueColumns=\n\t"
                + String.join(
                "\n\t",
                uniqueColumns.stream()
                    .map(ColumnReference::toString)
                    .collect(Collectors.toSet())))
        .add(
            "relations=\n\t"
                + String.join(
                "\n\t", relations.stream().map(Relation::toString).collect(Collectors.toSet())))
        .add("provider=\n\t'" + provider + "'")
        .toString();
  }
}
