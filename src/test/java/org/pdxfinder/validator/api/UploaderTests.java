package org.pdxfinder.validator.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pdxfinder.MockXssfWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UploaderTests {

  private MockMvc mockMvc;

  private static final String UPLOADER_URL = "/validation/upload";
  private static final String FILENAME = "metadata.xlsx";
  private static final String CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Autowired
  void UploadTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  public void when_postToUploader_then_expectJson() throws Exception {
    ResultMatcher ok = MockMvcResultMatchers.status().isOk();
    String tempWorkbook = MockXssfWorkbook.createStandardizedTempWorkbook(5).getAbsolutePath();
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file", FILENAME, CONTENT_TYPE, Files.readAllBytes(Path.of(tempWorkbook)));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart(UPLOADER_URL).file(mockMultipartFile))
        .andExpect(ok)
        .andExpect(content().string("[null,null,null,null,null,null]"));
  }

  @Test
  public void when_postToUploaderNoContent_then_returnNoContent() throws Exception {
    ResultMatcher noContent = MockMvcResultMatchers.status().isNoContent();
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", FILENAME, CONTENT_TYPE, "".getBytes());
    mockMvc
        .perform(MockMvcRequestBuilders.multipart(UPLOADER_URL).file(mockMultipartFile))
        .andExpect(noContent);
  }

  @Test
  public void when_postToUploaderNoContent_then_returnUnsupportedMediaType() throws Exception {
    ResultMatcher unsupportedMediaType = MockMvcResultMatchers.status().isUnsupportedMediaType();
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", FILENAME, "application/json", "test body".getBytes());
    mockMvc
        .perform(MockMvcRequestBuilders.multipart(UPLOADER_URL).file(mockMultipartFile))
        .andExpect(unsupportedMediaType);
  }
}
