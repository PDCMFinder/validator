package org.pdxfinder.validator.tablevalidation.error_creators;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error.DuplicateValueErrorBuilder;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

@Component
public class DuplicateValueErrorCreator extends ErrorCreator {

  public List<ValidationError> generateErrors(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    for (ColumnReference tested : tableSetSpecification.getUniqueColumns()) {
      Set<String> duplicates = findDuplicatesInColumn(columnFromTableSet(tableSet, tested));

      if (CollectionUtils.isNotEmpty(duplicates)) {
        errors.add(create(tested, duplicates));
      }
    }
    return errors;
  }

  private StringColumn columnFromTableSet(
      Map<String, Table> tableSet, ColumnReference columnReference) {
    return tableSet.get(columnReference.table()).stringColumn(columnReference.column());
  }

  private Set<String> findDuplicatesInColumn(StringColumn column) {
    return findDuplicates(column.asList());
  }

  private Set<String> findDuplicates(List<String> listContainingDuplicates) {
    final Set<String> duplicates = new HashSet<>();
    final Set<String> set1 = new HashSet<>();
    for (String string : listContainingDuplicates) {
      if (!set1.add(string)) {
        duplicates.add(string);
      }
    }
    return duplicates;
  }

  public ValidationError create(
      ColumnReference uniqueColumn, Set<String> duplicates) {
    return new DuplicateValueErrorBuilder(uniqueColumn, duplicates).build();
  }
}
