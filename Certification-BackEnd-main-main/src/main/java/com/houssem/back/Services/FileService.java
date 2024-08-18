package com.houssem.back.Services;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
public interface FileService {
    String save(MultipartFile file);
    Resource getFile(String fileName);
}
