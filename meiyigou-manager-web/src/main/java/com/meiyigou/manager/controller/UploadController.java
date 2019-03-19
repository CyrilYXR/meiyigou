package com.meiyigou.manager.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    //@Value("${FILE_SERVICE_URL}")
    private String file_service_url="http://192.168.25.133/";

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        String originalFileName = file.getOriginalFilename();
        // 扩展名
        String extName = originalFileName.substring(originalFileName.lastIndexOf(".")+1);

        try{
            FastDFSClient client = new FastDFSClient("classpath:spring/fdfs_client.conf");
            String fildId = client.uploadFile(file.getBytes(), extName);
            String url = file_service_url + fildId;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}
