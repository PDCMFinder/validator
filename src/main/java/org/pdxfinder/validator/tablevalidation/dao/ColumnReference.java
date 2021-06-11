package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pdxfinder.validator.tablevalidation.Relation;
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
  private Relation relation;

  @JsonCreator
  public ColumnReference(
      @JsonProperty("name") String columnName,
      @JsonProperty("charset") String charset,
      @JsonProperty("categories") List<String> categories,
      @JsonProperty("attributes") List<String> attributes,
      @JsonProperty("relation") List<String> relation
  ) {
    this.columnName = columnName;
    this.charset = createCharsetRestriction(charset);
    this.attributes = attributes;
    this.categories = createCategories(categories);
    this.relation = createRelation(relation);
  }

  private ValueRestrictions createCategories(List<String> categories) {
    return (categories != null) ? ValueRestrictions.of(categories)
        : ValueRestrictions.createEmpty();
  }

  private ValueRestrictions createCharsetRestriction(String charset) {
    ValueRestrictions charsetRestriction = Charsets.MISSING.getValueRestriction();
    if (charset != null) {
      Charsets valueRestriction = Charsets.getValueRestrictionFor(charset);
      charsetRestriction = valueRestriction.getValueRestriction();
    }
    return charsetRestriction;
  }

  private Relation createRelation(List<String> relationArgs) {
    Relation relation = Relation.createEmpty();
    if (relationArgs != null && relationArgs.size() == 3) {
      var validityType = Relation.ValidityType.parseValidityType(relationArgs.get(0));
      String table = relationArgs.get(1);
      String columnName = relationArgs.get(2);
      relation = Relation
          .betweenTableColumns(validityType, this, ColumnReference.of(table, columnName));
    } else if (relationArgs != null && relationArgs.size() != 0) {
      throw new IllegalArgumentException(
          String.format("Inappropriate format or content of %s", relationArgs));
    }
    return relation;
  }

  public ColumnReference(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  public static ColumnReference of(String tableName, String columnName) {
    return new ColumnReference(tableName, columnName);
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String table() {
    return this.tableName;
  }

  public String column() {
    return this.columnName;
  }

  public boolean hasCharset(Charsets charsetResriction) {
    return !charset.equals(charsetResriction);
  }

  public boolean hasCategories() {
    return categories.getErrorDescription().equalsIgnoreCase("");
  }

  public ValueRestrictions getCategories() {
    return categories;
  }

  public Relation getRelation() {
    return relation;
  }

  public boolean hasAttribute(Rules rule) {
    return attributes.contains(rule.name());
  }

  public List<String> getAttributes() {
    return attributes;
  }

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
