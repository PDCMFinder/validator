package org.pdxfinder.validator.tablevalidation.enums;

import java.util.Arrays;

public enum RelationType {
  TABLE_KEY,
  ONE_TO_ONE,
  ONE_TO_MANY,
  MISSING;

  public static RelationType parseRelationType(String type) {
    return Arrays.stream(RelationType.values())
        .filter(e -> e.name().equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}


