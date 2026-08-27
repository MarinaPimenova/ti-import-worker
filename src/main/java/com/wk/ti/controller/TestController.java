package com.wk.ti.controller;

import com.wk.ti.upload.TestService;
import com.wk.ti.upload.model.FileProcessingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rest/v1/import")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileProcessingResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(testService.upload(file));
    }
}
