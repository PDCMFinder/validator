package org.pdxfinder.validator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication()
@EnableCaching
public class Application implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    var webApplicationType = CommandCli.ParseWebApplicationType(args);
    application.setWebApplicationType(webApplicationType);
    application.run(args);
  }

  @Override
  public void run(String... arg) throws Exception {
    if (CommandCli.inLocalMode(arg)) {

    }
  }
}
