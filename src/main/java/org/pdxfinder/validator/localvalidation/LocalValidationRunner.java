package org.pdxfinder.validator.localvalidation;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.pdxfinder.validator.CommandCli;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetCleaner;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class LocalValidationRunner implements CommandLineRunner {

  private ValidationRunnerService validationRunnerService;
  private VariationValidationService variationValidation;

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
      var metadataSpecification = getTablSetSpecificiation(metadataWorkbookName, providerName);
      var allMetadataTables = readTablesAndCleanTables(providerPath, metadataWorkbookName);
      validationRunnerService.validateWorkbook(providerPath, allMetadataTables,
          metadataSpecification,
          metadataWorkbookName);
      if (molecularMetadataExists(providerPath) && molecularMetadataIsInTableset(
          allMetadataTables)) {
        var molecularMetadataSpecification = getTablSetSpecificiation(molecularMetadataName,
            providerName);
        validationRunnerService.validateWorkbook(providerPath, allMetadataTables,
            molecularMetadataSpecification, molecularMetadataName);
        variationValidation.validateVariantData(providerPath, allMetadataTables);
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

  private boolean molecularMetadataExists(Path providerPath) {
    int urlLen = providerPath.getNameCount();
    Path providerName = providerPath.getName(urlLen - 1);
    String metatadaWorkbook = String.format("%s/%s_molecular_metadata.xlsx", providerPath,
        providerName.getFileName());
    File metadataWorkbookFile = new File(metatadaWorkbook);
    return metadataWorkbookFile.exists();
  }


}
