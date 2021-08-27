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

  public TableSetSpecificationBuilder(String workbook) {
    pdcmWorkbook = PdxWorkbookCollection
        .fromYaml(Yml.WORKBOOK_COLLECTION.Location())
        .getWorkbook(workbook);
  }

  public TableSetSpecification generate() {
    Set<String> metadataTables = pdcmWorkbook.getTableNames();
    Set<ColumnReference> uniqIdColumns = getUniqueColumns();
    Set<ColumnReference> essentialColumns = getEssentialColumns();
    Map<Set<ColumnReference>, ValueRestrictions> regexRestrictions = regexRestrictions();
    Map<Set<ColumnReference>, ValueRestrictions> categoricalRestrictions =
        getCategoricalRestrictions();
    Set<Relation> relations = getRelations();

    return TableSetSpecification.create()
        .addRequiredTables(metadataTables)
        .addRequiredColumns(essentialColumns)
        .addNonEmptyColumns(essentialColumns)
        .addAllValueRestrictions(regexRestrictions)
        .addAllValueRestrictions(categoricalRestrictions)
        .addUniqueColumns(uniqIdColumns)
        .addRelations(relations);
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

  private Set<ColumnReference> getEssentialColumns() {
    return pdcmWorkbook.getAllColumnsWithAttribute(Rules.ESSENTIAL);
  }
}
