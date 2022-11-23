package org.pdxfinder.validator.tablevalidation.enums;

import java.util.Arrays;

public enum RelationType {
  TABLE_KEY,
  TABLE_KEY_MANY_TO_ONE,
  ONE_TO_ONE,
  ONE_TO_MANY,
  MISSING,
  MODEL_SAMPLE;

  public static RelationType parseRelationType(String type) {
    return Arrays.stream(RelationType.values())
        .filter(e -> e.name().equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Could not find relation of type: %s", type)));
  }
}


