package org.pdxfinder.validator.tablevalidation.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pdxfinder.validator.tablevalidation.error.ValidationErrorBuilder;

public class ValidationError {

  @SerializedName("type")
  @Expose
  private String type;

  @SerializedName("tableName")
  @Expose
  private String tableName;

  @SerializedName("tableReport")
  @Expose
  private TableReport tableReport = null;

  public TableReport getTableReport() {
    return tableReport;
  }


  public void setTableReport(TableReport tableReport) {
    this.tableReport = tableReport;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
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
        .append("message", tableReport.getColumnReport().getMessage())
        .append("columName", tableReport.getColumnReport().getMessage())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(tableReport.getColumnReport().getMessage())
        .append(tableReport.getColumnReport().getMessage())
        .toHashCode();
  }

  public static <T extends ValidationErrorBuilder> String getMessageFromValidationError(
      T validationError) {
    return validationError
        .getValidationError()
        .getTableReport()
        .getColumnReport()
        .getMessage();
  }
}
