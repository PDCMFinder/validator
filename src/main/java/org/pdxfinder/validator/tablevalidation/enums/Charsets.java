package org.pdxfinder.validator.tablevalidation.enums;

import org.pdxfinder.validator.tablevalidation.ValueRestrictions;

import java.util.Arrays;

public enum Charsets {
  MISSING("",
          ""
  ),
  FREE_TEXT(
          "^[\\p{Alpha}\\p{Space}\\p{Digit}().',<>%:;_\\/-]+$",
          "characters must be contained in the US ASCII Alphanumeric set and in these symbols ().',:;-/"),
  URL_SAFE(
          "^[\\p{Alpha}\\p{Digit}\\p{Space}._~-]+$",
          "characters must be contained in the US ASCII Alphanumeric set and in these symbols: ._~-"
  ),
  NUMERIC(
          "^[\\p{Digit}\\p{Space}pP\\.,-]+|not provided$",
          "characters must be contained in the US ASCII numbers set and the in these symbols: pP-.,"
  ),
  COLLECTION_EVENT(
          "(?i)(^collection event [0-9]{1,3})?|not provided",
          "Value must be of the correct format. Format:collection event [0-9]. This value can be blan"
  ),
  COLLECTION_DATE(
      "[A-Za-z]{3} [0-9]{4}|not provided",
          "Value must be of the correct format. Format:[MMM YYYY]"
  ),
  PMID_FORMAT(
      "(?i)^(?:pmid:\\s?[0-9]{1,8},?\\s?)*$",
          "Value must be of the correct format. Requires comma separated list if multiple values. Format: PMID: [8 digit id]"
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
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Could not find matching Enum.Charset for %s", charset)));
  }
}


