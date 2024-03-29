package org.pdxfinder.validator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;

public class CommandCli {

  private static final Logger log = LoggerFactory.getLogger(CommandCli.class);
  private static CommandLineParser parser = new DefaultParser();
  private static Options options =
      new Options()
          .addOption(
              null,
              "local",
              false,
              "Run validator in local mode. " + "Do not pass to run in microservice mode")
          .addOption(
              "d",
              "dir",
              true,
              "Will run on single provider folder. For full load pass the UPDOG directory. Requires --local");

  private CommandCli() {
  }

  public static WebApplicationType parseWebApplicationType(String[] args) {
    WebApplicationType webApplicationType = WebApplicationType.SERVLET;
    if (inLocalMode(args)) {
      webApplicationType = WebApplicationType.NONE;
      log.info("Running in microservice mode. Pass \"local\" to run locally");
    }
    return webApplicationType;
  }

  public static boolean inLocalMode(String[] args) {
    boolean isLocal = false;
    try {
      CommandLine line = parser.parse(options, args);
      isLocal = line.hasOption("local");
    } catch (ParseException exp) {
      log.error(String.format("%s%s", "Parsing failed.  Reason: ", exp.getMessage()));
    }
    return isLocal;
  }

  public static String getTargetDirectory(String[] args) {
    String directory = "";
    try {
      CommandLine line = parser.parse(options, args);
      boolean hasDir = line.hasOption("dir");
      if (hasDir) {
        directory = (String) line.getParsedOptionValue("dir");
      } else {
        log.error("Provider data path required. Use --dir=/path/to/provider");
      }
    } catch (ParseException exp) {
      log.error(String.format("%s%s", "Parsing failed.  Reason: ", exp.getMessage()));
    }
    return directory;
  }
}
