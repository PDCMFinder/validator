package org.pdxfinder.validator.tablevalidation.error_creators;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error.MissingValueErrorBuilder;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class MissingValueErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (ColumnReference tested : tableSetSpecification.getRequiredNonEmptyColumns()) {
      Table table = tableSet.get(tested.table());
      Table missing = table.where(table.column(tested.column()).isMissing());
      if (missing.rowCount() > 0) {
        int[] missingRowNumbers = table.column(tested.column()).isMissing().toArray();
        String missRowNumberPrint = Arrays.toString(shiftMissingRowNumbers(missingRowNumbers));
        errors.add(
            create(tested, missRowNumberPrint)
        );
      }
    }
    return errors;
  }

  public ValidationError create(
      ColumnReference columnReference,
      String missingRowNumbers) {
    return new MissingValueErrorBuilder(columnReference, missingRowNumbers).build();
  }

  public int[] shiftMissingRowNumbers(int[] missingRowNumbers) {
    return IntStream.range(0, missingRowNumbers.length)
        .map(i -> missingRowNumbers[i] + 6)
        .toArray();
  }
}
