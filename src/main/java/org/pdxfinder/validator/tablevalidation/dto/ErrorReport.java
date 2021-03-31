package org.pdxfinder.validator.tablevalidation.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class ErrorReport {

  @SerializedName("UUID")
  @Expose
  private String uuid;

  @SerializedName("tableErrors")
  @Expose
  private List<ValidationError> validationErrors;

  public ErrorReport() {
    this.uuid = UUID.randomUUID().toString();
  }

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
  }
}
