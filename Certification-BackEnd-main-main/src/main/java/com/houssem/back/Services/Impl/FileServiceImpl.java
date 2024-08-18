package com.houssem.back.Services.Impl;

import com.houssem.back.Services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.path}")
    private String filePath;

    @Override
    public String save(MultipartFile file) {
        // Define the directory where files will be stored
        String dir = System.getProperty("user.dir") + "/" + filePath;
        File directory = new File(dir);

        // Create the directory if it does not exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Define the file path including file name
        File targetFile = new File(dir + "/" + file.getOriginalFilename());
        try {
            // Save the file to the target location
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }

        // Return the name of the saved file
        return file.getOriginalFilename();
    }

    @Override
    public Resource getFile(String fileName) {
        // Define the path to the file
        String dir = System.getProperty("user.dir") + "/" + filePath + "/" + fileName;
        Path path = Paths.get(dir);

        try {
            // Create a UrlResource for the file
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
