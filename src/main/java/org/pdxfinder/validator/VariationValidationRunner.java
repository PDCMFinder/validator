package org.pdxfinder.validator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.pdxfinder.validator.tableutilities.FileReader;
import org.pdxfinder.validator.tablevalidation.TableSetSpecification;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class VariationValidationRunner {

  private ValidationRunnerService validationRunnerService;

  @Autowired
  public void VariationValidationRunner(ValidationRunnerService validationRunnerService) {
    this.validationRunnerService = validationRunnerService;
  }

  public void validateVariantData(Path providerPath) {
    String variantWorkbooksRe = "(mut|expression|cna|cyto|drug|treatment)";
    Map<String, List<Path>> workbookFiles = new HashMap<>();
    var variantWorkbooks = validationRunnerService.getWorkbooks(variantWorkbooksRe);
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
            validationRunnerService.getTablSetSpecificiation(entry.getKey()));
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
    validationRunnerService.validateWorkbook(providerpath, Map.of(tableName, table),
        tableSetSpecification, table.name());
  }

  private List<Path> getAllMatchingFilesFromWorkBookTitle(String workbookTitle,
      Path updogProviderDirectory) {
    var matchingGlobe = validationRunnerService.createPathMatchingGlobe(workbookTitle);
    return FileReader.findAllFilesIn(updogProviderDirectory, matchingGlobe);
  }
}
