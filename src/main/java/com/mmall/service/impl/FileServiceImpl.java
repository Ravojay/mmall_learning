package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * Created by ravojay on 1/9/20.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String filename = file.getOriginalFilename();
        String fileExtend = filename.substring(filename.lastIndexOf(".")+1);
        String uploadFilename = UUID.randomUUID().toString()+"."+fileExtend;
        logger.info("starting to upload,filename:{},path:{},newFileName:{}",filename,path,uploadFilename);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFIle = new File(path,uploadFilename);
        try{
            file.transferTo(targetFIle);
            //upload to ftp server
            FTPUtil.uploadFile(Lists.newArrayList(targetFIle));
            //delete file in tomcat
            targetFIle.delete();
        }catch (Exception e){
            logger.error("upload error",e);
            return null;
        }
        return targetFIle.getName();
    }
}
