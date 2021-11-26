package org.pdxfinder.validator.tablevalidation.error_creators;

import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error.MissingColumnErrorBuilder;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class MissingColumnErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (ColumnReference required : tableSetSpecification.getRequiredColumns()) {
      if (tableIsMissingColumn(tableSet, required)) {
        errors.add(create(required));
      }
    }
    return errors;
  }

  private boolean tableIsMissingColumn(
      Map<String, Table> tableSet, ColumnReference columnReference) {
    return !tableSet.get(columnReference.table()).columnNames().contains(columnReference.column());
  }

  public ValidationError create(ColumnReference columnReference) {
    return new MissingColumnErrorBuilder(columnReference).build();
  }
}
