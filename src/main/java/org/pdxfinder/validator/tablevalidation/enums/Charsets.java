package org.pdxfinder.validator.tablevalidation.enums;

import java.util.Arrays;
import org.pdxfinder.validator.tablevalidation.ValueRestrictions;

public enum Charsets {
  MISSING("",
      ""
  ),
  FREE_TEXT(
      "^[\\p{Alpha}\\p{Space}\\p{Digit}().',<>%:;_\\/-]+$",
      "have characters not contained in US ASCII Alphanumeric and ().',:;-/"),
  URL_SAFE(
      "^[\\p{Alpha}\\p{Digit}\\p{Space}._~-]+$",
      "have characters not contained in US ASCII Alphanumeric and ._~-"
  ),
  NUMERICAL(
      "^[\\p{Digit}\\p{Space}pP\\.,-]+$",
      "have characters not contains in US ASCII numbers and pP-.,"
  ),
  COLLECTION_EVENT(
      "(?i)(^collection event [0-9]{1,3})?",
      "Not of type: collection event [0-9] or blank"
  ),
  COLLECTION_DATE(
      "[A-Za-z]{3} [0-9]{4}",
      "not of type: [MMM YYYY] three letter month and 4 digit year"
  ),
  PMID_FORMAT(
      "(?i)^(?:pmid:\\s?[0-9]{8},?\\s?)*$",
      "not of type: PMID: [8 digit id] (comma separated)"
  );

  private ValueRestrictions charsetResriction;

  Charsets(String regex, String description) {
    charsetResriction = ValueRestrictions.of(regex, description);
  }

  public ValueRestrictions getValueRestriction() {
    return this.charsetResriction;
  }

  public static Charsets getValueRestrictionFor(String charset) {
    return Arrays.stream(Charsets.values())
        .filter(e -> e.name().equalsIgnoreCase(charset))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}


