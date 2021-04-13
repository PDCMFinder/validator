package org.pdxfinder.validator.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetUtilities;
import org.pdxfinder.validator.tablevalidation.ErrorReporter;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.Validator;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.rules.PdxValidationRuleset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.tablesaw.api.Table;

@Service
public class ValidationWebService {

  private TableSetSpecification pdxValidationRuleset;
  private static final Logger log = LoggerFactory.getLogger(ValidationWebService.class);

  public ValidationWebService() {
    this.pdxValidationRuleset = new PdxValidationRuleset().generate();
  }

  public String proccessRequest(MultipartFile multipartFile) {
    logRequest(multipartFile);
    Validator validator = new Validator();
    Map<String, Table> tableSet = getTables(multipartFile);
    var cleanedTableSet = TableSetUtilities.cleanPdxTables(tableSet);
    List<ValidationError> validationErrors =
        validator.validate(cleanedTableSet, pdxValidationRuleset);
    return new ErrorReporter(validationErrors).getJson();
  }

  private Map<String, Table> getTables(MultipartFile multipartFile) {
    List<Table> tables = new ArrayList<>();
    List<Table> cleanedTables = new ArrayList<>();
    try {
      tables = FileReader.readXlsx(multipartFile.getInputStream());
      cleanedTables = TableSetUtilities.cleanTableNames(tables);
    } catch (IOException e) {
      log.error("Error reading multipartfile into table ", e);
    }
    return FileReader.listToMap(cleanedTables);
  }

  private void logRequest(MultipartFile multipartFile) {
    String logMessage =
        String.format(
            "Filname uploaded: %s  time: %s",
            multipartFile.getName(), Calendar.getInstance().getTime());
    log.info(logMessage);
  }
}
