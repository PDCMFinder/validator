package org.pdxfinder.validator.localvalidation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tableutilities.TableUtilities;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.Table;

@Service
public class VariationValidationService {

  private ValidationRunnerService validationRunnerService;
  private Map<String, Table> molecularMetadata;

  @Autowired
  public void variationValidationRunner(ValidationRunnerService validationRunnerService) {
    this.validationRunnerService = validationRunnerService;
  }

  public void validateVariantData(Path providerPath, Map<String, Table> molecularMetadata) {
    String variantWorkbooksRe = "(mut|expression|cna|cyto|drug|treatment)";
    Map<String, List<Path>> workbookFiles = new HashMap<>();
    this.molecularMetadata = molecularMetadata;
    var variantWorkbooks = validationRunnerService.getWorkbooks(variantWorkbooksRe);
    for (Workbook workbook : variantWorkbooks) {
      String workbookTitle = workbook.getWorkbookTitle();
      var matchingFiles = getAllMatchingFilesFromWorkBookTitle(workbookTitle, providerPath);
      if (!matchingFiles.isEmpty()) {
        workbookFiles.put(workbookTitle, matchingFiles);
      }
    }
    validateVariantFiles(workbookFiles, providerPath);
  }

  private void validateVariantFiles(Map<String, List<Path>> workbookFiles, Path providerPath) {
    workbookFiles.entrySet()
        .forEach(validationEachFileByVariantWorkbook(providerPath));
  }

  private Consumer<Entry<String, List<Path>>> validationEachFileByVariantWorkbook(
      Path providerPath) {
    String providerName = providerPath.getFileName().toString();
    return entry ->
        validateForEachVariantFile(providerPath, entry.getKey(), entry.getValue(),
            validationRunnerService.getTablSetSpecificiation(entry.getKey(), providerName));
  }

  private void validateForEachVariantFile(Path providerPath, String tableName, List<Path> filepaths,
      TableSetSpecification tableSetSpecification) {
    filepaths.stream()
        .map(path -> FileReader.readTsvOrReturnEmpty(path.toFile()))
        .filter(Predicate.not(Table::isEmpty))
        .forEach(
            table -> validateVariantTable(providerPath, tableName, table, tableSetSpecification));
  }

  private void validateVariantTable(Path providerpath, String tableName, Table table,
      TableSetSpecification tableSetSpecification) {
    var variantTable = new HashMap<String, Table>();
    variantTable.put(tableName, table);
    var variantTableWithMolecularMetadata = TableUtilities.mergeTableMaps(variantTable,
        molecularMetadata);
    validationRunnerService.validateWorkbook(providerpath, variantTableWithMolecularMetadata,
        tableSetSpecification, table.name());
  }

  private List<Path> getAllMatchingFilesFromWorkBookTitle(String workbookTitle,
      Path updogProviderDirectory) {
    var matchingGlobe = validationRunnerService.createPathMatchingGlobe(workbookTitle);
    return FileReader.findAllFilesIn(updogProviderDirectory, matchingGlobe);
  }
}
