package org.pdxfinder.validator.tablevalidation.errorCreators;

import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.errorBuilders.MissingTableErrorBuilder;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.util.List;
import java.util.Map;

@Component
public class MissingTableErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
          Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (String table : tableSetSpecification.getMissingTablesFrom(tableSet)) {
      errors.add(
              new MissingTableErrorBuilder(table).build());
    }
    return errors;
  }

  public MissingTableErrorBuilder create(String tableName) {
    return new MissingTableErrorBuilder(tableName);
  }
}
