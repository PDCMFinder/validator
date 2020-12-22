package org.pdxfinder.validator.tablevalidation.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ErrorReport {

  @SerializedName("tableErrors")
  @Expose
  private List<TableErrors> tableErrors;

  public List<TableErrors> getTableErrors() {
    return tableErrors;
  }

  public void setTableErrors(List<TableErrors> tableErrors) {
    this.tableErrors = tableErrors;
  }
}
