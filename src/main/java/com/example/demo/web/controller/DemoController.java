package com.example.demo.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class DemoController {
    @PostMapping(path = "/v1/demo/upload", consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> upload(@RequestParam("file") MultipartFile[] multipartFile) {
        return Map.of("uploadedFileSize", Long.toString(multipartFile[0].getSize()));
    }
}
