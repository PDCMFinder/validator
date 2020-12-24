package org.pdxfinder.validator.tablevalidation.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
