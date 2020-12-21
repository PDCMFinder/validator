package org.pdxfinder.validator.tablevalidation.error;

import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ErrorCreator {

    protected List<ValidationError> errors = new ArrayList<>();

    abstract List<ValidationError> generateErrors(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    );

}
