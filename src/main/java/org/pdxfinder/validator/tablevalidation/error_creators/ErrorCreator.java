package org.pdxfinder.validator.tablevalidation.error_creators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import tech.tablesaw.api.Table;

public abstract class ErrorCreator {

  protected List<ValidationError> errors = new ArrayList<>();

  abstract List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification);
}
