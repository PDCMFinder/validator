package org.pdxfinder.validator.tablevalidation.dao;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;
import org.pdxfinder.validator.tablevalidation.enums.Charsets;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColumnReference {

  private String tableName;
  private String columnName;
  private List<String> attributes;
  private ValueRestrictions charset;
  private ValueRestrictions categories;
  private List<Relation> relations;

  @JsonCreator
  public ColumnReference(
      @JsonProperty("name") String columnName,
      @JsonProperty("charset") String charset,
      @JsonProperty("categories") List<String> categories,
      @JsonProperty("attributes") List<String> attributes,
      @JsonProperty("relation") List<Relation> relations
  ) {
    this.columnName = columnName;
    this.charset = createCharsetRestriction(charset);
    this.attributes = defaultIfNull(attributes, new ArrayList<>());
    this.categories = createCategories(categories);
    this.relations = defaultIfNull(relations, new ArrayList<>());
  }

  @JsonIgnore
  public void init() {
    applyNamesToRelations();
  }

  @JsonIgnore
  private void applyNamesToRelations() {
    relations.forEach(x ->
        x.addLeftTableAndColumn(tableName, columnName));
  }

  @JsonIgnore
  private ValueRestrictions createCategories(List<String> categories) {
    return (categories != null) ? ValueRestrictions.of(categories)
        : ValueRestrictions.createEmpty();
  }

  @JsonIgnore
  private ValueRestrictions createCharsetRestriction(String charset) {
    ValueRestrictions charsetRestriction = Charsets.MISSING.getValueRestriction();
    if (charset != null) {
      Charsets valueRestriction = Charsets.getValueRestrictionFor(charset);
      charsetRestriction = valueRestriction.getValueRestriction();
    }
    return charsetRestriction;
  }

  @JsonIgnore
  public ColumnReference(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  @JsonIgnore
  public static ColumnReference of(String tableName, String columnName) {
    return new ColumnReference(tableName, columnName);
  }

  @JsonIgnore
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  @JsonIgnore
  public String table() {
    return this.tableName;
  }

  @JsonIgnore
  public String column() {
    return this.columnName;
  }

  @JsonIgnore
  public boolean hasCharset(Charsets charsetRestriction) {
    String errorDescription = charsetRestriction.getValueRestriction().getErrorDescription();
    return charset.getErrorDescription().equals(errorDescription);
  }

  @JsonIgnore
  public boolean hasCategories() {
    return !categories.getErrorDescription().equalsIgnoreCase("");
  }

  @JsonIgnore
  public ValueRestrictions getCategories() {
    return categories;
  }

  @JsonIgnore
  public List<Relation> getRelation() {
    return relations;
  }

  @JsonIgnore
  public ValueRestrictions getCharset() {
    return charset;
  }

  @JsonIgnore
  public void setCharset(ValueRestrictions charset) {
    this.charset = charset;
  }

  @JsonIgnore
  public boolean hasAttribute(Rules rule) {
    return attributes.stream()
        .anyMatch(s -> s.equalsIgnoreCase(rule.name()));

  }

  @JsonIgnore
  public List<String> getAttributes() {
    return attributes;
  }

  @JsonIgnore
  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ColumnReference that = (ColumnReference) o;

    return new EqualsBuilder()
        .append(tableName, that.tableName)
        .append(columnName, that.columnName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(tableName).append(columnName).toHashCode();
  }

  @Override
  public String toString() {
    return String.format("%s > %s", table(), column());
  }
}
