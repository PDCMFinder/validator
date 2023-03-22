package org.pdxfinder.validator.localvalidation;

import org.pdxfinder.validator.CommandCli;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetCleaner;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public class LocalValidationRunner implements CommandLineRunner {

  private ValidationRunnerService validationRunnerService;
  private VariationValidationService variationValidation;
  private static final Logger log = LoggerFactory.getLogger(LocalValidationRunner.class);

  @Autowired
  LocalValidationRunner(ValidationRunnerService validationRunnerService,
      VariationValidationService variationValidation) {
    this.validationRunnerService = validationRunnerService;
    this.variationValidation = variationValidation;
  }

  @Override
  public void run(String... arg) {
    if (CommandCli.inLocalMode(arg)) {
      String targetDirUrl = CommandCli.getTargetDirectory(arg);
      List<String> directories = FileReader.getDirectories(targetDirUrl);
      validateDirectories(directories);
    }
  }

  public void validateDirectories(List<String> directories) {
    String metadataWorkbookName = "metadata";
    String molecularMetadataName = "molecular_metadata";
    for (String provider : directories) {
      Path providerPath = Path.of(provider);
      String providerName = providerPath.getFileName().toString();
      var allMetadataTables = readTablesAndCleanTables(providerPath, metadataWorkbookName);
      var metadataSpecification = getTablSetSpecificiation(metadataWorkbookName, providerName);
      if(allMetadataTables.containsKey("cell_model") & !allMetadataTables.containsKey("pdx_model")){
        metadataWorkbookName = "metadata_cell";
        metadataSpecification = TableSetSpecification.merge(metadataSpecification, getTablSetSpecificiation(metadataWorkbookName, providerName));
        log.info("No pdx model information found.");
      } else if (allMetadataTables.containsKey("pdx_model") & !allMetadataTables.containsKey("cell_model")) {
        metadataWorkbookName = "metadata_pdx";
        metadataSpecification = TableSetSpecification.merge(metadataSpecification, getTablSetSpecificiation(metadataWorkbookName, providerName));
        log.info("No cell model information found.");
      }else if (allMetadataTables.containsKey("pdx_model") & allMetadataTables.containsKey("cell_model")){
        metadataSpecification = TableSetSpecification.merge(metadataSpecification, getTablSetSpecificiation("metadata_pdx", providerName),
                getTablSetSpecificiation("metadata_cell", providerName));
      }else{
        log.info("No model information table found");
      }
      metadataSpecification.setTablesetSpecificationName("metadata");
      validationRunnerService.validateWorkbook(providerPath, allMetadataTables,
              metadataSpecification, metadataWorkbookName);
      if (molecularMetadataIsInTableset(
              allMetadataTables)) {
        var molecularMetadataSpecification = getTablSetSpecificiation(molecularMetadataName,
                providerName);
        validationRunnerService.validateWorkbook(providerPath, allMetadataTables,
                molecularMetadataSpecification, molecularMetadataName);
        variationValidation.validateVariantData(providerPath, allMetadataTables);
      } else {
        log.info("Complete molecular metadata not found");
      }

    }
  }

  private boolean molecularMetadataIsInTableset(Map<String, Table> allMetadataTables) {
    return allMetadataTables.containsKey("sample") && allMetadataTables.containsKey("platform")
        && allMetadataTables.containsKey("platform_web");
  }

  private TableSetSpecification getTablSetSpecificiation(String workbookName, String providerName) {
    return validationRunnerService.getTablSetSpecificiation(workbookName, providerName);
  }

  private Map<String, Table> readTablesAndCleanTables(Path providerPath, String workbookName) {
    Map<String, Table> metadataTableSet = validationRunnerService.readMetadataTablesFromPath(
        providerPath.toAbsolutePath(),
        workbookName);
    return TableSetCleaner.cleanPdxTables(metadataTableSet);
  }
}
