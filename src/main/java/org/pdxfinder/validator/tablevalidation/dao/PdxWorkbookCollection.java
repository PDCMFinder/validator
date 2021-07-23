package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdxWorkbookCollection {

  private static final Logger log = LoggerFactory.getLogger(PdxWorkbookCollection.class);

  @JsonProperty("workbooks")
  private List<Workbook> workbooks;

  public static PdxWorkbookCollection fromYaml(String yamlUrl) {
    PdxWorkbookCollection workbook = new PdxWorkbookCollection();
    var mapper = new ObjectMapper(new YAMLFactory());
    try {
      workbook = mapper.readValue(new File(yamlUrl), PdxWorkbookCollection.class);
    } catch (IOException ex) {
      log.error("Failure to map yml {}", yamlUrl, ex);
    }
    return workbook;
  }

  public Workbook getWorkbook(String workbook_title) {
    return workbooks.stream()
        .filter(worbook -> worbook.getWorkbookTitle().matches(workbook_title))
        .findFirst()
        .orElseGet(Workbook::new);
  }

  public int len() {
    return workbooks.size();
  }

  public List<Workbook> getWorkbooks() {
    return workbooks;
  }

  public void setWorkbooks(List<Workbook> workbooks) {
    this.workbooks = workbooks;
  }

}
