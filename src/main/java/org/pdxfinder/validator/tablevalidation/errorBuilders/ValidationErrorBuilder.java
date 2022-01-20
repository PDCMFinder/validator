package org.pdxfinder.validator.tablevalidation.errorBuilders;

import org.pdxfinder.validator.tablevalidation.dto.ValidationError;

public abstract class ValidationErrorBuilder<T extends ValidationErrorBuilder<?>> {

  public abstract T buildCause(String cause);

  public abstract T buildRule(String rule);

  public abstract ValidationError build();

}
