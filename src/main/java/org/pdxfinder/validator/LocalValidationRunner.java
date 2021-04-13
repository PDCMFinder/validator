package org.pdxfinder.validator;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class LocalValidationRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(LocalValidationRunner.class);

  @Override
  public void run(String... arg) {
    if (CommandCli.inLocalMode(arg)) {
      String targetDirUrl = CommandCli.getTargetDirectory(arg);
      List<String> directories = FileReader.getDirectories(targetDirUrl);
      validateDirectories(directories);
    }
  }

  public void validateDirectories(List<String> directories) {
    TableSetSpecification pdxValidationRuleset = new PdxValidationRuleset().generate();
    for (String provider : directories) {
      Map<String, Table> tableSet = readPdxTablesFromPath(Path.of(provider).toAbsolutePath());
      Validator validator = new Validator();
      var cleanedTables = TableSetUtilities.cleanPdxTables(tableSet);
      List<ValidationError> validationErrors = validator
          .validate(cleanedTables, pdxValidationRuleset);
      log.info(new ErrorReporter(validationErrors).getJson());
    }
  }

  private Map<String, Table> readPdxTablesFromPath(Path updogProviderDirectory) {
    PathMatcher metadataFiles = FileSystems.getDefault()
        .getPathMatcher("glob:**{metadata-,sampleplatform}*.tsv");
    return FileReader.readAllTsvFilesIn(updogProviderDirectory, metadataFiles);
  }
}
