package org.pdxfinder.validator.api;

import java.util.Optional;
import org.pdxfinder.validator.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  @PostMapping("upload")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    Optional<MultipartFile> optionalFile = Optional.ofNullable(file);
    HttpStatus responseStatus = HttpStatus.OK;
    String entity = "{ \"Error\" : \"HttpStatus Error\" }";
    if (multipartFileIsMissing(optionalFile)) {
      responseStatus = HttpStatus.NO_CONTENT;
    } else if (multipartFileIsUnsupportedType(optionalFile)) {
      responseStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    } else {
      entity = validatorService.proccessRequest(optionalFile.get());
    }
    return new ResponseEntity<>(entity, responseStatus);
  }

  private boolean multipartFileIsMissing(Optional<MultipartFile> multipartFile) {
    return multipartFile.isEmpty() || multipartFile.get().isEmpty()
        || multipartFile.get().getContentType() == null;
  }

  private boolean multipartFileIsUnsupportedType(Optional<MultipartFile> multipartFile) {
    return !multipartFile.get().getContentType().equals(EXCEL_CONTENT_TYPE);
  }

}
