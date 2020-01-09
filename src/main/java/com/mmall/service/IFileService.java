package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ravojay on 1/9/20.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
