package org.pdxfinder.validator.tablevalidation.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TableReport {

  @SerializedName("column")
  @Expose
  private ColumnReport columnReport;

  public ColumnReport getColumnReport() {
    return columnReport;
  }

  public void setColumnReport(ColumnReport columnReport) {
    this.columnReport = columnReport;
  }
}
