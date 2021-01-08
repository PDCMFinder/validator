package org.pdxfinder.validator.tablevalidation.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ColumnReport {

  @SerializedName("message")
  @Expose
  private String message;

  @SerializedName("columnName")
  @Expose
  private String columnName;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
}
