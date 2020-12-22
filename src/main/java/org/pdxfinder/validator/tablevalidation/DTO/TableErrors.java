package org.pdxfinder.validator.tablevalidation.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TableErrors {

  @SerializedName("type")
  @Expose
  private String type;

  @SerializedName("tableName")
  @Expose
  private List<TableName> tableName = null;

  public List<TableName> getTableName() {
    return tableName;
  }

  public void setTableName(List<TableName> tableName) {
    this.tableName = tableName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
