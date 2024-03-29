package org.pdxfinder.validator.tablevalidation;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ValueRestrictions {

  private Predicate<String> predicate;
  private String errorDescription;
  private boolean canBeEmpty = true;

  private ValueRestrictions(String regex, String description) {
    this.predicate = regexToPredicate(regex);
    this.errorDescription = description;
  }

  private ValueRestrictions(Predicate<String> predicate, String errorDescription) {
    this.predicate = predicate;
    this.errorDescription = errorDescription;
  }

  public static ValueRestrictions of(String regexCharset, String charSetDescription) {
    return new ValueRestrictions(regexCharset, charSetDescription);
  }

  public static ValueRestrictions of(List<String> categories) {
    String errorDescription = buildErrorDescription(categories);
    return new ValueRestrictions(listToCaseInsensitivePredicate(categories), errorDescription);
  }

  public static ValueRestrictions createEmpty() {
    return new ValueRestrictions("", "");
  }

  private static String buildErrorDescription(List<String> categories) {
    return String.format(
        "not in a required category. " + "Required Categories: [%s] Value found",
        String.join(",", categories));
  }

  private static Predicate<String> listToCaseInsensitivePredicate(List<String> categories) {
    String orRegex = String.join("|", categories);
    String builtRegex = anchoredNoGroupingCaseInsensitiveRegex(orRegex);
    return regexToPredicate(builtRegex);
  }

  private static String anchoredNoGroupingCaseInsensitiveRegex(String orRegex) {
    return String.format("(?i)^(?:%s)$", orRegex);
  }

  private static Predicate<String> regexToPredicate(String regex) {
    return Pattern.compile(regex).asPredicate();
  }

  public Predicate<String> getEmptyFilter() {
    Predicate<String> emptyFilter = String::isEmpty;
    return (canBeEmpty) ? emptyFilter.negate() : emptyFilter;
  }

  public Predicate<String> getPredicate() {
    return predicate.negate();
  }

  public String getErrorDescription() {
    return errorDescription;
  }

}
