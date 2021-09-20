package org.pdxfinder.validator.tablevalidation.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdxWorkbookCollection {

  private static final Logger log = LoggerFactory.getLogger(PdxWorkbookCollection.class);

  @JsonProperty("workbooks")
  private List<Workbook> workbooks;

  public static PdxWorkbookCollection fromYaml(String yamlUrl) {
    PdxWorkbookCollection workbookCollection = new PdxWorkbookCollection();
    var mapper = new ObjectMapper(new YAMLFactory());
    try {
      workbookCollection = mapper.readValue(new File(yamlUrl), PdxWorkbookCollection.class);
    } catch (IOException ex) {
      log.error("Failure to map yml {}", yamlUrl, ex);
    }
    workbookCollection.init();
    return workbookCollection;
  }

  private void init() {
    workbooks.forEach(Workbook::init);
  }

  public Workbook getWorkbook(String workbookRegex) {
    return getWorkbooks(workbookRegex).stream()
        .findFirst()
        .orElseGet(Workbook::new);
  }

  public List<Workbook> getWorkbooks(String workbookRegex) {
    return workbooks.stream()
        .filter(Objects::nonNull)
        .filter(workbook -> workbook.getWorkbookTitle().matches(workbookRegex))
        .collect(Collectors.toList());
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
