package org.pdxfinder.validator.tablevalidation.enums;

import java.util.Arrays;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;

public class CharsetRestrictions {

  static final String NOTCOLLECTED = "not collected";
  static final String NOTPROVIDED = "not provided";

  private CharsetRestrictions() {
  }

  private static final ValueRestrictions ETHNICITY_ASSESSMENT_CATEGORIES =
      ValueRestrictions.of(Arrays.asList("self-assessed", "genetic", getNOTPROVIDED()));

  private static final ValueRestrictions TUMOUR_TYPE_CATEGORIES =
      ValueRestrictions.of(
          Arrays.asList(
              "primary",
              "metastatic",
              "recurrent",
              "refactory",
              getNOTCOLLECTED(),
              getNOTPROVIDED()));

  private static final ValueRestrictions SHARE_CATEGORIES =
      ValueRestrictions.of(Arrays.asList("yes", "no", getNOTPROVIDED()));

  private static final ValueRestrictions TREATMENT_NAIVE_AT_COLLECTION_CATEGORIES =
      ValueRestrictions.of(
          Arrays.asList(
              "treatment naive", "not treatment naive", getNOTCOLLECTED(), getNOTPROVIDED()));

  private static final ValueRestrictions priorTreatmentCategories =
      ValueRestrictions.of(Arrays.asList("yes", "no", getNOTPROVIDED(), getNOTCOLLECTED()));

  private static final ValueRestrictions providerTypeCategories =
      ValueRestrictions.of(
          Arrays.asList("academia", "industry", "academia and industry", "CRO", "pharma"));


  public enum Yml {
    WORKBOOK_COLLECTION("./data/WorkbookCollection.yml");
    private String fileUrl;

    Yml(String fileLocation) {
      fileUrl = fileLocation;
    }

    public String location() {
      return fileUrl;
    }
  }

  public static String getNOTCOLLECTED() {
    return NOTCOLLECTED;
  }

  public static String getNOTPROVIDED() {
    return NOTPROVIDED;
  }

  public static ValueRestrictions getEthnicityAssessmentCategories() {
    return ETHNICITY_ASSESSMENT_CATEGORIES;
  }

  public static ValueRestrictions getTumourTypeCategories() {
    return TUMOUR_TYPE_CATEGORIES;
  }

  public static ValueRestrictions getShareCategories() {
    return SHARE_CATEGORIES;
  }

  public static ValueRestrictions getTreatmentNaiveAtCollectionCategories() {
    return TREATMENT_NAIVE_AT_COLLECTION_CATEGORIES;
  }

  public static ValueRestrictions getPriorTreatmentCategories() {
    return priorTreatmentCategories;
  }

  public static ValueRestrictions getProviderTypeCategories() {
    return providerTypeCategories;
  }
}
