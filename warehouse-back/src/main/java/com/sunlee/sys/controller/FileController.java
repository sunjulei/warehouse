package com.sunlee.sys.controller;

import cn.hutool.core.date.DateUtil;
import com.sunlee.sys.common.AppFileUtils;
import com.sunlee.sys.common.ResultObj;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: sunlee
 * @Date: 2026/04/10 23:46
 */
@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    /**
     * 允许上传的文件扩展名白名单
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp"
    ));

    /**
     * 允许上传的文件 MIME 类型白名单
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    ));

    /**
     * 最大允许上传文件大小：5MB
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 文件上传
     * @param mf
     * @return
     */
    @RequestMapping("uploadFile")
    public Object uploadFile(MultipartFile mf) {
        // 0.校验文件是否为空
        if (mf == null || mf.isEmpty()) {
            return ResultObj.error("上传文件不能为空");
        }

        // 1.校验文件大小
        if (mf.getSize() > MAX_FILE_SIZE) {
            return ResultObj.error("文件大小不能超过5MB");
        }

        // 2.校验文件扩展名
        String oldName = mf.getOriginalFilename();
        if (oldName == null || oldName.isEmpty()) {
            return ResultObj.error("文件名不能为空");
        }
        String extension = getFileExtension(oldName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResultObj.error("不允许的文件类型，仅支持: jpg, jpeg, png, gif, bmp, webp");
        }

        // 3.校验 MIME 类型
        String contentType = mf.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return ResultObj.error("文件类型校验失败");
        }

        // 4.根据旧的文件名生成新的文件名
        String newName = AppFileUtils.createNewFileName(oldName);
        // 5.得到当前日期的字符串
        String dirName = DateUtil.format(new Date(), "yyyy-MM-dd");
        // 6.构造文件夹
        File dirFile = new File(AppFileUtils.UPLOAD_PATH, dirName);
        // 7.判断当前文件夹是否存在
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        // 8.构造文件对象
        File file = new File(dirFile, newName + "_temp");
        // 9.把mf里面的图片信息写入file
        try {
            mf.transferTo(file);
        } catch (IllegalStateException | IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ResultObj.error("文件上传失败");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("path", dirName + "/" + newName + "_temp");
        return map;
    }

    /**
     * 获取文件扩展名（不含点号）
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    /**
     * 图片下载
     */
    @RequestMapping("showImageByPath")
    public ResponseEntity<Object> showImageByPath(String path) {
        // 校验路径参数
        if (path == null || path.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        // 校验路径中不包含越级遍历字符
        if (path.contains("..") || path.contains("//") || path.startsWith("/") || path.startsWith("\\")) {
            log.warn("检测到非法路径访问: {}", path);
            return ResponseEntity.badRequest().build();
        }
        return AppFileUtils.createResponseEntity(path);
    }

}
