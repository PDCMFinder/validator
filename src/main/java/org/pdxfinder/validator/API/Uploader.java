package org.pdxfinder.validator.API;

import org.pdxfinder.validator.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/validation/")
public class Uploader {

  private ValidatorService validatorService;
  private static final String EXCEL_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Autowired
  Uploader(ValidatorService validatorService) {
    this.validatorService = validatorService;
  }

  @PostMapping(path = "upload", consumes = "multipart/form-data")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile) {
    HttpStatus responseStatus = HttpStatus.OK;
    if (multipartFile.isEmpty()) {
      responseStatus = HttpStatus.NO_CONTENT;
    } else if (!multipartFile.getContentType().equals(EXCEL_CONTENT_TYPE)) {
      responseStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    } else {
      validatorService.proccessRequest(multipartFile);
    }
    return new ResponseEntity<>(responseStatus);
  }
}
