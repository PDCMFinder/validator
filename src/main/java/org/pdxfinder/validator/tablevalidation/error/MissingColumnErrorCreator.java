package org.pdxfinder.validator.tablevalidation.error;

import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class MissingColumnErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (ColumnReference required : tableSetSpecification.getRequiredColumns()) {
      if (tableIsMissingColumn(tableSet, required)) {
        errors.add(create(required, tableSetSpecification.getProvider()).getValidationError());
      }
    }

    return errors;
  }

  private boolean tableIsMissingColumn(
      Map<String, Table> tableSet, ColumnReference columnReference) {
    return !tableSet.get(columnReference.table()).columnNames().contains(columnReference.column());
  }

  public MissingColumnError create(ColumnReference columnReference, String provider) {
    return new MissingColumnError(columnReference, provider);
  }
}
