package org.pdxfinder;

import org.junit.Assert;
import org.junit.Test;
import org.pdxfinder.validator.CommandCli;
import org.springframework.boot.WebApplicationType;

public class CommandCliTest {

  @Test
  public void Given_NoArguments_ThenDefaultToServlet() {
    String[] args = new String[0];
    Assert.assertEquals(WebApplicationType.SERVLET, CommandCli.ParseWebApplicationType(args));
  }

  @Test
  public void Given_LocalIsPassed_WhenApplicationIsRun_ThenTurnOffWebApplication() {
    String[] args = {"--local"};
    Assert.assertEquals(WebApplicationType.NONE, CommandCli.ParseWebApplicationType(args));
  }

  @Test
  public void Given_dirIsPassed_ThenReturnDirString() {
    String expectedDir = "/path/to/data";
    String[] args = {String.format("--dir=%s", expectedDir)};
    Assert.assertEquals(expectedDir, CommandCli.getTargetDirectory(args));
  }
}
