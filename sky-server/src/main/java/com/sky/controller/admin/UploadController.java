package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class UploadController {

    private final AliOssUtil aliOssUtil;

    public UploadController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file){
        log.info("文件上传：{}",file);
        String originalFilename = file.getOriginalFilename();
        log.info("原始文件名：{}",originalFilename);
        String url = null;
        try {
            String objectName= UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
            url = aliOssUtil.upload(file.getBytes(), objectName);
        }catch (Exception e){
            log.error("文件上传失败：{}",e.getMessage());
            return Result.error("上传失败");
        }

        return Result.success(url);
    }
}
