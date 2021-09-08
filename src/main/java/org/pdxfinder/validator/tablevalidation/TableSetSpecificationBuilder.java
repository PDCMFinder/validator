package org.pdxfinder.validator.tablevalidation;


import java.util.Map;
import java.util.Set;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.pdxfinder.validator.tablevalidation.enums.CharsetRestrictions.Yml;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

public class TableSetSpecificationBuilder {

  private Workbook pdcmWorkbook;

  public TableSetSpecificationBuilder(Workbook pdcmWorkbook) {
    this.pdcmWorkbook = pdcmWorkbook;
  }

  public TableSetSpecificationBuilder(String pdcmWorkbook) {
    var pdxWorkbookCollection = PdxWorkbookCollection.fromYaml(Yml.WORKBOOK_COLLECTION.location());
    this.pdcmWorkbook = pdxWorkbookCollection.getWorkbook(pdcmWorkbook);
  }

  public TableSetSpecification generate() {
    Set<String> metadataTables = pdcmWorkbook.getTableNames();
    Set<ColumnReference> uniqIdColumns = getUniqueColumns();
    Set<ColumnReference> requiredColumns = getAllColumns();
    Set<ColumnReference> notEmptyColumns = getNotEmptyColumns();
    Map<Set<ColumnReference>, ValueRestrictions> regexRestrictions = regexRestrictions();
    Map<Set<ColumnReference>, ValueRestrictions> categoricalRestrictions =
        getCategoricalRestrictions();
    Set<Relation> relations = getRelations();

    return TableSetSpecification.create()
        .addRequiredTables(metadataTables)
        .addRequiredColumns(requiredColumns)
        .addNonEmptyColumns(notEmptyColumns)
        .addAllValueRestrictions(regexRestrictions)
        .addAllValueRestrictions(categoricalRestrictions)
        .addUniqueColumns(uniqIdColumns)
        .addRelations(relations)
        .setTablesetSpecificationName(this.pdcmWorkbook.getWorkbookTitle());
  }

  private Set<Relation> getRelations() {
    return pdcmWorkbook.getAllColumnRelations();
  }

  private Map<Set<ColumnReference>, ValueRestrictions> getCategoricalRestrictions() {
    return pdcmWorkbook.getAllColumnsByCategories();
  }

  private Map<Set<ColumnReference>, ValueRestrictions> regexRestrictions() {
    return pdcmWorkbook.getColumnsByCharset();
  }

  private Set<ColumnReference> getUniqueColumns() {
    return pdcmWorkbook.getAllColumnsWithAttribute(Rules.UNIQUE);
  }

  private Set<ColumnReference> getNotEmptyColumns() {
    return pdcmWorkbook.getAllColumnsWithAttribute(Rules.NOT_EMPTY);
  }

  private Set<ColumnReference> getAllColumns() {
    return pdcmWorkbook.getAllTableColumns();
  }
}
