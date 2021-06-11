package org.pdxfinder.validator;

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
    TableSetSpecification pdxValidationRuleset = new TableSetSpecificationBuilder().generate();
    for (String provider : directories) {
      Path providerPath = Path.of(provider);
      Map<String, Table> tableSet = readPdxTablesFromPath(providerPath.toAbsolutePath());
      var cleanedTables = TableSetCleaner.cleanPdxTables(tableSet);
      validationService.validate(cleanedTables, pdxValidationRuleset);
      log.info(validationService.getJsonReport(providerPath.getFileName().toString()));
    }
  }

  private Map<String, Table> readPdxTablesFromPath(Path updogProviderDirectory) {
    PathMatcher metadataFiles =
        FileSystems.getDefault().getPathMatcher("glob:**{metadata-,sampleplatform}*.tsv");
    return FileReader.readAllTsvFilesIn(updogProviderDirectory, metadataFiles);
  }
}
