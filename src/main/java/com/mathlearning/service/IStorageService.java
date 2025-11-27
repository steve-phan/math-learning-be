package com.mathlearning.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IStorageService {
    void ensureBucketExists();

    String uploadFile(MultipartFile file, String folder) throws IOException;

    String getFileUrl(String fileName);
}
