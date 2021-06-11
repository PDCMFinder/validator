package org.pdxfinder.validator.tablevalidation;


import java.util.Map;
import java.util.Set;
import org.pdxfinder.validator.tablevalidation.dao.ColumnReference;
import org.pdxfinder.validator.tablevalidation.dao.PdxWorkbookCollection;
import org.pdxfinder.validator.tablevalidation.dao.Workbook;
import org.pdxfinder.validator.tablevalidation.enums.CharsetRestrictions.Yml;
import org.pdxfinder.validator.tablevalidation.enums.Rules;

public class TableSetSpecificationBuilder {

  private Workbook metadataWorkbook;
  private static final String METADATA_WORKBOOK = "metadata";

  public TableSetSpecificationBuilder() {
    metadataWorkbook = PdxWorkbookCollection
        .fromYaml(Yml.WORKBOOK_COLLECTION.Location())
        .getWorkbook(METADATA_WORKBOOK);
  }

  public TableSetSpecification generate() {
    Set<String> metadataTables = metadataWorkbook.getTableNames();
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
    return metadataWorkbook.getAllColumnRelations();
  }

  private Map<Set<ColumnReference>, ValueRestrictions> getCategoricalRestrictions() {
    return metadataWorkbook.getAllColumnsByCategories();
  }

  private Map<Set<ColumnReference>, ValueRestrictions> regexRestrictions() {
    return metadataWorkbook.getColumnsByCharset();
  }

  private Set<ColumnReference> getUniqueColumns() {
    return metadataWorkbook.getAllColumnsWithAttribute(Rules.UNIQUE);
  }

  private Set<ColumnReference> getEssentialColumns() {
    return metadataWorkbook.getAllColumnsWithAttribute(Rules.ESSENTIAL);
  }
}
