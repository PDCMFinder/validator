package org.pdxfinder.validator.api;

import org.pdxfinder.validator.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/validation/")
public class Uploader {

  private ValidatorService validatorService;
  private static final String EXCEL_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Autowired
  Uploader(ValidatorService validatorService) {
    this.validatorService = validatorService;
  }

  @PostMapping(
      path = "upload",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile) {
    HttpStatus responseStatus = HttpStatus.OK;
    String entity = "{ \"Error\" : \"HttpStatus Error\" }";
    if (multipartFile.isEmpty() || multipartFile.getContentType() == null) {
      responseStatus = HttpStatus.NO_CONTENT;
    } else if (!multipartFile.getContentType().equals(EXCEL_CONTENT_TYPE)) {
      responseStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    } else {
      entity = validatorService.proccessRequest(multipartFile);
    }
    return new ResponseEntity<>(entity, responseStatus);
  }
}
