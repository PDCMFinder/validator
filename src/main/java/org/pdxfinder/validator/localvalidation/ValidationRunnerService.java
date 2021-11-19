package org.pdxfinder.validator.localvalidation;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.TableSetSpecificationBuilder;
import org.pdxfinder.validator.tablevalidation.ValidationService;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.pdxfinder.validator.tablevalidation.enums.CharsetRestrictions.Yml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.Table;


@Service
public class ValidationRunnerService {

  private static final Logger log = LoggerFactory.getLogger(ValidationRunnerService.class);

  private ValidationService validationService;
  private PdxWorkbookCollection pdxWorkbookCollection;

  @Autowired
  ValidationRunnerService(ValidationService validationService) {
    this.validationService = validationService;
    InputStream inputStream = getClass().getResourceAsStream(Yml.WORKBOOK_COLLECTION.location());
    this.pdxWorkbookCollection = PdxWorkbookCollection.fromYaml(inputStream);
  }

  public PathMatcher createPathMatchingGlobe(String workbookName) {
    var workbookNameGlobe = String.format("glob:**%s*.tsv", workbookName);
    return FileSystems.getDefault().getPathMatcher(workbookNameGlobe);
  }

  public Map<String, Table> readMetadataTablesFromPath(Path updogProviderDirectory,
      String workbookName) {
    PathMatcher metadataFiles = createPathMatchingGlobe(workbookName);
    return FileReader.readAllTsvFilesIn(updogProviderDirectory, metadataFiles);
  }

  public void validateWorkbook(Path providerPath, Map<String, Table> cleanedMetadataTables,
      TableSetSpecification tableSetSpecification, String reportName) {
    validationService.validate(cleanedMetadataTables, tableSetSpecification);
    String reportId = String.format("Provider:%s Filename:%s",
        providerPath.getFileName().toString(), reportName);
    log.info(validationService.getJsonReport(reportId));
  }

  public TableSetSpecification getTablSetSpecificiation(String workbookName, String providerName) {
    var workbook = pdxWorkbookCollection.getWorkbook(workbookName);
    return new TableSetSpecificationBuilder(workbook).setProvider(providerName).build();
  }

  public List<Workbook> getWorkbooks(String workbookRegex) {
    return pdxWorkbookCollection.getWorkbooks(workbookRegex);
  }
}
