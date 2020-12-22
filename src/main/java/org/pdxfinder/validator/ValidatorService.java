package org.pdxfinder.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tableSetUtilities.TableReader;
import org.pdxfinder.validator.tablevalidation.ErrorReporter;
import org.pdxfinder.validator.tablevalidation.Validator;
import org.pdxfinder.validator.tablevalidation.rules.PdxValidationRuleset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.tablesaw.api.Table;

@Service
public class ValidatorService {

  private Validator validator;
  private static final Logger log = LoggerFactory.getLogger(ValidatorService.class);

  public ValidatorService(Validator validator) {
    this.validator = validator;
  }

  public String proccessRequest(MultipartFile multipartFile) {
    logRequest(multipartFile);
    Map<String, Table> tableSet = getTables(multipartFile);
    var cleanedTableSet = TableReader.cleanPdxTables(tableSet);
    var validationErrors =
        validator.validate(cleanedTableSet, new PdxValidationRuleset().generate());
    return new ErrorReporter(validationErrors).getJson();
  }

  private Map<String, Table> getTables(MultipartFile multipartFile) {
    List<Table> tables = new ArrayList<>();
    try {
      tables = TableReader.readXlsx(multipartFile.getInputStream());
    } catch (IOException e) {
      log.error("Error reading multipartfile into table ", e);
    }
    return TableReader.listToMap(tables);
  }

  private void logRequest(MultipartFile multipartFile) {
    String logMessage =
        String.format(
            "Filname uploaded: %s  time: %s",
            multipartFile.getName(), Calendar.getInstance().getTime());
    log.info(logMessage);
  }
}
