package org.pdxfinder.validator;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetCleaner;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class LocalValidationRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(LocalValidationRunner.class);
  private ValidationRunnerService validationRunnerService;
  private VariationValidationRunner variationValidationRunner;
  private Map<String, Table> metadataTables;
  private Map<String, Table> allMolecularMetadataTables;

  @Autowired
  LocalValidationRunner(ValidationRunnerService validationRunnerService,
      VariationValidationRunner variationValidationRunner) {
    this.validationRunnerService = validationRunnerService;
    this.variationValidationRunner = variationValidationRunner;
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
      var metadataSpecification = getTablSetSpecificiation(metadataWorkbookName);
      metadataTables = readTablesAndCleanTables(providerPath, metadataWorkbookName);
      validationRunnerService.validateWorkbook(providerPath, metadataTables, metadataSpecification,
          metadataWorkbookName);
      if (molecularMetadataExists(providerPath)) {
        var molecularMetadataSpecification = getTablSetSpecificiation(molecularMetadataName);
        var cleanedMolecularMetadataTables = readTablesAndCleanTables(providerPath,
            molecularMetadataName);
        validationRunnerService.validateWorkbook(providerPath, cleanedMolecularMetadataTables,
            molecularMetadataSpecification, molecularMetadataName);
        variationValidationRunner.validateVariantData(providerPath);
      }
    }
  }

  private TableSetSpecification getTablSetSpecificiation(String workbookName) {
    return validationRunnerService.getTablSetSpecificiation(workbookName);
  }

  private Map<String, Table> readTablesAndCleanTables(Path providerPath, String workbookName) {
    Map<String, Table> metadataTableSet = validationRunnerService.readMetadataTablesFromPath(
        providerPath.toAbsolutePath(),
        workbookName);
    return TableSetCleaner.cleanPdxTables(metadataTableSet);
  }

  private boolean molecularMetadataExists(Path providerPath) {
    int urlLen = providerPath.getNameCount();
    Path providerName = providerPath.getName(urlLen - 1);
    String metatadaWorkbook = String.format("%s/%s_molecular_metadata.xlsx", providerPath,
        providerName.getFileName());
    File metadataWorkbookFile = new File(metatadaWorkbook);
    return metadataWorkbookFile.exists();
  }


}
