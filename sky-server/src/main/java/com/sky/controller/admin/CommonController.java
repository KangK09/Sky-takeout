package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();

            // 获取文件扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一文件名
            String objectName = UUID.randomUUID().toString() + extension;

            // 本地存储路径（注意：确保目录存在，且有写权限）
            String basePath = "D:\\Desktop\\Sky-takeout\\file\\";  // 🧠 根据你电脑改成合适目录
            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs(); // 自动创建目录
            }

            // 拼接完整路径
            File dest = new File(basePath + objectName);

            // 保存文件到本地
            file.transferTo(dest);

            // 返回文件路径或文件名
            return Result.success(objectName);  // 或 basePath + objectName
        } catch (IOException e) {
            log.error("文件上传失败: {}", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
