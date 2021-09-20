package org.pdxfinder.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication()
@EnableCaching
public class Application {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    var webApplicationType = CommandCli.parseWebApplicationType(args);
    application.setWebApplicationType(webApplicationType);
    application.run(args);
  }
}
