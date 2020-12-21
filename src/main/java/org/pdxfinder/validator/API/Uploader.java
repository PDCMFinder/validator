package org.pdxfinder.validator.API;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/validation/")
public class Uploader {

  @PostMapping(path = "upload",consumes = "multipart/form-data")
  public ResponseEntity<String> upload(
      @RequestParam("file") MultipartFile multipartFile
  ) {
    System.out.println(multipartFile.getName());
    System.out.println(multipartFile.getContentType());
    return ResponseEntity.ok("Success");
  }


}
