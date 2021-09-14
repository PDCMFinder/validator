package org.pdxfinder.validator.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetCleaner;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.TableSetSpecificationBuilder;
import org.pdxfinder.validator.tablevalidation.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.tablesaw.api.Table;

@Service
public class ValidationWebService {

  private TableSetSpecification pdxValidationRuleset;
  private ValidationService validationService;

  private static final Logger log = LoggerFactory.getLogger(ValidationWebService.class);

  @Autowired
  public ValidationWebService(ValidationService validationService) {
    this.validationService = validationService;
    this.pdxValidationRuleset = new TableSetSpecificationBuilder("metadata").generate();
  }

  public String proccessRequest(MultipartFile multipartFile) {
    logRequest(multipartFile);
    Map<String, Table> tableSet = getTables(multipartFile);
    var cleanedTableSet = TableSetCleaner.cleanPdxTables(tableSet);
    validationService.validate(cleanedTableSet, pdxValidationRuleset);
    return validationService.getJsonReport();
  }

  private Map<String, Table> getTables(MultipartFile multipartFile) {
    List<Table> tables = new ArrayList<>();
    Map<String, Table> cleanedTables = new HashMap<>();
    try {
      tables = FileReader.readXlsx(multipartFile.getInputStream());
      cleanedTables = TableSetCleaner.cleanPdxTables(FileReader.listToMap(tables));
    } catch (IOException e) {
      log.error("Error reading multipartfile into table ", e);
    }
    return cleanedTables;
  }

  private void logRequest(MultipartFile multipartFile) {
    String logMessage =
        String.format(
            "Filname uploaded: %s  time: %s",
            multipartFile.getName(), Calendar.getInstance().getTime());
    log.info(logMessage);
  }
}
