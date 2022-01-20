package org.pdxfinder.validator.tablevalidation.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ValidationError {

  @SerializedName("tableName")
  @Expose
  private String tableName;

  @SerializedName("columnName")
  @Expose
  private String columnName;

  @SerializedName("row")
  @Expose
  private String row;

  @SerializedName("errorType")
  @Expose
  private String errorType;

  @SerializedName("rule")
  @Expose
  private String rule;

  @SerializedName("cause")
  @Expose
  private String cause;

  private ValidationError(Builder builder) {
    this.tableName = builder.tableName;
    this.columnName = builder.columnName;
    this.row = builder.row;
    this.errorType = builder.errorType;
    this.rule = builder.rule;
    this.cause = builder.cause;
  }

  public String getErrorType() {
    return errorType;
  }

  public String getTableName() {
    return tableName;
  }

  public String getCause() {
    return cause;
  }

  public String getRule() {
    return rule;
  }

  public String getColumnName() {
    return columnName;
  }

  public String getRow() {
    return row;
  }

  public static class Builder {
    public String tableName;
    public String columnName;
    public String row;
    public String errorType;
    public String rule;
    public String cause;

    public Builder(String ErrorType) {
      this.errorType = ErrorType;
    }

    public Builder setTableName(String tableName) {
      this.tableName = tableName;
      return this;
    }

    public Builder setColumnName(String columnName) {
      this.columnName = columnName;
      return this;
    }

    public Builder setRow(String row) {
      this.row = row;
      return this;
    }

    public Builder setErrorType(String errorType) {
      this.errorType = errorType;
      return this;
    }

    public Builder setRule(String rule) {
      this.rule = rule;
      return this;
    }

    public Builder setCause(String cause) {
      this.cause = cause;
      return this;
    }

    public ValidationError build() {
      return new ValidationError(this);
    }

  }

  @Override
  public boolean equals(Object o) {

    if (o == this) {
      return true;
    }
    if (!(o instanceof ValidationError)) {
      return false;
    }

    return new EqualsBuilder()
            .append(((ValidationError) o).getTableName(), tableName)
            .append(((ValidationError) o).getColumnName(), columnName)
            .append(((ValidationError) o).getRow(), row)
            .append(((ValidationError) o).getErrorType(), errorType)
            .append(((ValidationError) o).getRule(), rule)
            .append(((ValidationError) o).getCause(), cause)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(tableName)
            .append(columnName)
            .append(row)
            .append(errorType)
            .append(rule)
            .append(cause)
            .toHashCode();
  }

}