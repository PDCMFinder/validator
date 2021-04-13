package org.pdxfinder.validator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.WebApplicationType;

public class CommandCli {

  private static CommandLineParser parser = new DefaultParser();
  private static Options options =
      new Options()
          .addOption(
              null,
              "local",
              false,
              "Run validator in local mode. " + "Do not pass to run in microservice mode")
          .addOption("d", "--data_dir", true, "pass single provider folder or complete UPDOG")
          .addOption("a", "--all", false, "Run on full data directory")
          .addOption("g", "--group", true, "Run on single providers folder");

  public static WebApplicationType ParseWebApplicationType(String[] args) {
    WebApplicationType webApplicationType = WebApplicationType.SERVLET;
    if (inLocalMode(args)) {
      webApplicationType = WebApplicationType.NONE;
      System.out.println("Running in microservice mode. Pass \"local\" to run locally");
    }
    return webApplicationType;
  }

  public static boolean inLocalMode(String[] args) {
    boolean isLocal = false;
    try {
      CommandLine line = parser.parse(options, args);
      isLocal = line.hasOption("local");
    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
    }
    return isLocal;
  }
}
