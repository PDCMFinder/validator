package org.pdxfinder.validator.tablevalidation.error_creators;

import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error.MissingTableError;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class MissingTableErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (String table : tableSetSpecification.getMissingTablesFrom(tableSet)) {
      errors.add(
          new MissingTableError(table).getValidationError());
    }
    return errors;
  }

  public MissingTableError create(String tableName) {
    return new MissingTableError(tableName);
  }
}
