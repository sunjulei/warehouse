package com.sunlee.sys.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: sunlee
 * @Date: 2026/04/10 23:44
 */
@Slf4j
public class AppFileUtils {

    /**
     * 文件上传的保存路径  默认值
     */
    public static String UPLOAD_PATH = "G:/upload/";

    static {
        //通过反射的方式，读取配置文件的存储地址
        InputStream stream = AppFileUtils.class.getClassLoader().getResourceAsStream("file.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            log.error("操作失败: {}", e.getMessage(), e);
        }
        String property = properties.getProperty("filepath");
        if (null != property) {
            UPLOAD_PATH = property;
        }
    }

    /**
     * 根据文件老名字得到新名字
     *
     * @param oldName 文件老名字
     * @return 新名字由32位随机数加图片后缀组成
     */
    public static String createNewFileName(String oldName) {
        //获取文件名后缀
        String stuff = oldName.substring(oldName.lastIndexOf("."), oldName.length());
        //将UUID与文件名后缀进行拼接，生成新的文件名  生成的UUID为32位
        return IdUtil.simpleUUID().toUpperCase() + stuff;
    }

    /**
     * 文件下载
     *
     * @param path
     * @return
     */
    public static ResponseEntity<Object> createResponseEntity(String path) {
        //1,构造文件对象
        File file = new File(UPLOAD_PATH, path);
        if (file.exists()) {
            //将下载的文件，封装byte[]
            byte[] bytes = null;
            try {
                bytes = FileUtil.readBytes(file);
            } catch (Exception e) {
                log.error("操作失败: {}", e.getMessage(), e);
            }
            //创建封装响应头信息的对象
            HttpHeaders header = new HttpHeaders();
            //根据文件扩展名设置正确的Content-Type
            String contentType = getContentType(path);
            header.setContentType(MediaType.parseMediaType(contentType));
            //创建ResponseEntity对象
            ResponseEntity<Object> entity = new ResponseEntity<Object>(bytes, header, HttpStatus.OK);
            return entity;
        }
        return null;
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private static String getContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    /**
     * 更该图片的名字 去掉_temp
     *
     * @param goodsImg
     * @return
     */
    public static String renameFile(String goodsImg) {
        File file = new File(UPLOAD_PATH, goodsImg);
        String replace = goodsImg.replace("_temp", "");
        if (file.exists()) {
            file.renameTo(new File(UPLOAD_PATH, replace));
        }
        return replace;
    }

    /**
     * 根据老路径删除图片
     *
     * @param oldPath
     */
    public static void removeFileByPath(String oldPath) {
        if (oldPath == null || oldPath.isEmpty()) {
            return;
        }
        //图片的路径不是默认图片的路径
        if (!oldPath.equals(Constast.DEFAULT_IMG_GOODS)) {
            File file = new File(UPLOAD_PATH, oldPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }


}
