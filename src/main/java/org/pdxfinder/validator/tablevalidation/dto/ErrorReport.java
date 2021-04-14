package org.pdxfinder.validator.tablevalidation.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class ErrorReport {

  @SerializedName("ID")
  @Expose
  private String ID;

  @SerializedName("tableErrors")
  @Expose
  private List<ValidationError> validationErrors;

  public ErrorReport() {
    this.ID = UUID.randomUUID().toString();
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
  }
}
