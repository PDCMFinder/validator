package org.pdxfinder.validator;

import org.springframework.boot.CommandLineRunner;

public class LocalValidationRunner implements CommandLineRunner {

  @Override
  public void run(String... arg) throws Exception {
    if (CommandCli.inLocalMode(arg)) {
    }
  }
}
