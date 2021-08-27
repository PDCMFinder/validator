package org.pdxfinder.validator;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class LocalValidationRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(LocalValidationRunner.class);
  private ValidationService validationService;

  @Autowired
  LocalValidationRunner(ValidationService validationService) {
    this.validationService = validationService;
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
      validateWorkbook(providerPath, metadataWorkbookName);
      if (molecularMetadataExists(providerPath)) {
        validateWorkbook(providerPath, molecularMetadataName);
      }
    }
  }

  private boolean molecularMetadataExists(Path providerPath) {
    int urlLen = providerPath.getNameCount();
    Path providerName = providerPath.getName(urlLen - 1);
    String metatadaWorkbook = String.format("%s/%s_molecular_metadata.xlsx", providerPath,
        providerName.getFileName());
    File metadataWorkbookFile = new File(metatadaWorkbook);
    return metadataWorkbookFile.exists();
  }

  private void validateWorkbook(Path providerPath, String metadataWorkbookName) {
    TableSetSpecification metadataTableSpecification = new TableSetSpecificationBuilder(
        metadataWorkbookName).generate();
    Map<String, Table> metadataTableSet = readPdxTablesFromPath(metadataWorkbookName,
        providerPath.toAbsolutePath());
    var cleanedMetadataTables = TableSetCleaner.cleanPdxTables(metadataTableSet);
    validationService.validate(cleanedMetadataTables, metadataTableSpecification);
    log.info(validationService.getJsonReport(providerPath.getFileName().toString()));
  }

  private Map<String, Table> readPdxTablesFromPath(String workbookName,
      Path updogProviderDirectory) {
    var workbookNameGlobe = String.format("glob:**%s-*.tsv", workbookName);
    PathMatcher metadataFiles =
        FileSystems.getDefault().getPathMatcher(workbookNameGlobe);
    return FileReader.readAllTsvFilesIn(updogProviderDirectory, metadataFiles);
  }
}
