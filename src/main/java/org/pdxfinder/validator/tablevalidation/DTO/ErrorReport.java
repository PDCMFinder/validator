package org.pdxfinder.validator.tablevalidation.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ErrorReport {

  @SerializedName("tableErrors")
  @Expose
  private List<ValidationError> validationErrors;

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
  }
}
