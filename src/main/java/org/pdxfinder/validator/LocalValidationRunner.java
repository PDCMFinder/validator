package org.pdxfinder.validator;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableSetCleaner;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.TableSetSpecificationBuilder;
import org.pdxfinder.validator.tablevalidation.ValidationService;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.pdxfinder.validator.tablevalidation.enums.CharsetRestrictions.Yml;
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
  private PdxWorkbookCollection pdxWorkbookCollection;

  @Autowired
  LocalValidationRunner(ValidationService validationService) {
    this.validationService = validationService;
  }

  @Override
  public void run(String... arg) {
    if (CommandCli.inLocalMode(arg)) {
      String targetDirUrl = CommandCli.getTargetDirectory(arg);
      List<String> directories = FileReader.getDirectories(targetDirUrl);
      pdxWorkbookCollection = PdxWorkbookCollection.fromYaml(Yml.WORKBOOK_COLLECTION.location());
      validateDirectories(directories);
    }
  }

  public void validateDirectories(List<String> directories) {
    String metadataWorkbookName = "metadata";
    String molecularMetadataName = "molecular_metadata";
    for (String provider : directories) {
      Path providerPath = Path.of(provider);
      var metadataSpecification = getTablSetSpecificiation(metadataWorkbookName);
      var cleanedMetadataTables = readTablesAndCleanTables(providerPath, metadataWorkbookName);
      validateWorkbook(providerPath, cleanedMetadataTables, metadataSpecification,
          metadataWorkbookName);
      if (molecularMetadataExists(providerPath)) {
        var molecularMetadataSpecification = getTablSetSpecificiation(molecularMetadataName);
        var cleanedMolecularMetadataTables = readTablesAndCleanTables(providerPath,
            molecularMetadataName);
        validateWorkbook(providerPath, cleanedMolecularMetadataTables,
            molecularMetadataSpecification, molecularMetadataName);
        validateVariantData(providerPath);
      }
    }
  }

  private Map<String, Table> readTablesAndCleanTables(Path providerPath, String workbookName) {
    Map<String, Table> metadataTableSet = readMetadataTablesFromPath(
        providerPath.toAbsolutePath(),
        workbookName);
    return TableSetCleaner.cleanPdxTables(metadataTableSet);
  }

  private void validateVariantData(Path providerPath) {
    String variantWorkbooksRe = "(mut|expression|cna|cyto|drug|treatment)";
    var variantWorkbooks = pdxWorkbookCollection.getWorkbooks(variantWorkbooksRe);
    Map<String, List<Path>> workbookFiles = new HashMap<>();
    for (Workbook workbook : variantWorkbooks) {
      String workbookTitle = workbook.getWorkbookTitle();
      workbookFiles.put(
          workbookTitle,
          getAllMatchingFilesFromWorkBookTitle(workbookTitle, providerPath)
      );
    }
    validateVariantFiles(workbookFiles, providerPath);
  }

  private void validateVariantFiles(Map<String, List<Path>> workbookFiles, Path providerPath) {
    workbookFiles.entrySet()
        .forEach(validationEachFileByVariantWorkbook(providerPath));
  }

  private Consumer<Entry<String, List<Path>>> validationEachFileByVariantWorkbook(
      Path providerPath) {
    return entry ->
        validateForEachVariantFile(providerPath, entry.getKey(), entry.getValue(),
            getTablSetSpecificiation(entry.getKey()));
  }

  private void validateForEachVariantFile(Path providerPath, String tableName, List<Path> filepaths,
      TableSetSpecification tableSetSpecification) {
    filepaths.stream()
        .map(path -> FileReader.readTsvOrReturnEmpty(path.toFile()))
        .forEach(
            table -> validateVariantTable(providerPath, tableName, table, tableSetSpecification));
  }

  private void validateVariantTable(Path providerpath, String tableName, Table table,
      TableSetSpecification tableSetSpecification) {
    validateWorkbook(providerpath, Map.of(tableName, table), tableSetSpecification, table.name());
  }

  private void validateWorkbook(Path providerPath, Map<String, Table> cleanedMetadataTables,
      TableSetSpecification tableSetSpecification, String reportName) {
    validationService.validate(cleanedMetadataTables, tableSetSpecification);
    String reportId = String.format("Provider:%s Filename:%s",
        providerPath.getFileName().toString(), reportName);
    log.info(validationService.getJsonReport(reportId));
  }

  private boolean molecularMetadataExists(Path providerPath) {
    int urlLen = providerPath.getNameCount();
    Path providerName = providerPath.getName(urlLen - 1);
    String metatadaWorkbook = String.format("%s/%s_molecular_metadata.xlsx", providerPath,
        providerName.getFileName());
    File metadataWorkbookFile = new File(metatadaWorkbook);
    return metadataWorkbookFile.exists();
  }

  private TableSetSpecification getTablSetSpecificiation(String workbookName) {
    var workbook = pdxWorkbookCollection.getWorkbook(workbookName);
    return new TableSetSpecificationBuilder(
        workbook).generate();
  }

  private List<Path> getAllMatchingFilesFromWorkBookTitle(String workbookTitle,
      Path updogProviderDirectory) {
    var matchingGlobe = createPathMatchingGlobe(workbookTitle);
    return FileReader.findAllFilesIn(updogProviderDirectory, matchingGlobe);
  }

  private PathMatcher createPathMatchingGlobe(String workbookName) {
    var workbookNameGlobe = String.format("glob:**%s*.tsv", workbookName);
    return FileSystems.getDefault().getPathMatcher(workbookNameGlobe);
  }

  private Map<String, Table> readMetadataTablesFromPath(Path updogProviderDirectory,
      String workbookName) {
    PathMatcher metadataFiles = createPathMatchingGlobe(workbookName);
    return FileReader.readAllTsvFilesIn(updogProviderDirectory, metadataFiles);
  }
}
