package com.yixi.yipan.common.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import com.yixi.yipan.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储工具类
 *
 * @author yixi
 */
@Slf4j
@Component
public class StorageUtils {
    private final BaseMapper<FileInfo> baseMapper;

    @Value("${file.path}")
    private String prePath;

    @Value("${server.domain}")
    private String domain;

    public StorageUtils(BaseMapper<FileInfo> baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * 计算文件的 MD5 值
     *
     * @param file MultipartFile 文件
     * @return MD5 哈希值的十六进制字符串
     */
    public String getFileMd5(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("文件为空，无法计算 MD5");
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream is = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    md.update(buffer, 0, read);
                }
            }

            // 转换为十六进制字符串
            byte[] md5Bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("计算文件 MD5 失败: {}", file.getOriginalFilename(), e);
            return null;
        }
    }

    /**
     * 获取下载链接
     *
     * @param fileId 文件路径
     * @return 下载链接
     */
    public String getDownloadUrl(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return null;
        }
        return domain + "/api/v1/files/download/" + fileId;
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件字节数组
     */
    public byte[] downloadFile(String filePath) {
        // 下载文件
        System.out.println("收到下载请求：路径：" + filePath);
        Path path = Path.of(filePath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("下载文件失败:{}", filePath, e);
            return null;
        }
    }

    /**
     * 生成文件路径
     *
     * @param fileInfo 文件信息
     * @return 文件路径
     */
    public String generateFilePath(FileInfo fileInfo) {



        // 创建路径结构：年/月/日/文件名_时间戳.扩展名
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String extension = FilenameUtils.getExtension(fileInfo.getFileName());

        return String.format("%s%s/%s/%s/%s_%s.%s",
                prePath,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                FilenameUtils.getBaseName(fileInfo.getFileName()),
                timestamp,
                extension);
    }

    /**
     * 保存文件
     *
     * @param file     文件
     * @param fileInfo 文件信息
     */
    public void saveFile(MultipartFile file, FileInfo fileInfo) {
        String filePath = this.generateFilePath(fileInfo);
        try {
            // 保存文件
            log.info("保存文件：{}", filePath);
            Files.createDirectories(Path.of(filePath).getParent());
            Files.write(Path.of(filePath), file.getBytes());

            log.info("保存文件成功:目录{}", filePath);
            // 保存文件信息到数据库
            baseMapper.insert(fileInfo);
        } catch (IOException e) {
            log.error("保存文件失败:{}", filePath, e);
        }
    }

    public MultipartFile convertFileToMultipartFile(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());
            String name = file.getName();

            // 读取文件内容
            byte[] content = Files.readAllBytes(file.toPath());

            // 创建 MockMultipartFile
            return new MockMultipartFile(
                    name,                   // 参数名
                    file.getName(),         // 原始文件名
                    contentType,            // 内容类型
                    content                 // 文件内容
            );
        } catch (IOException e) {
            throw new RuntimeException("转换文件失败：", e);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public void deleteFileInHarDisk(String filePath) {
        try {
            log.info("删除文件：{}", filePath);
            Files.delete(Paths.get(filePath));
            log.info("删除文件成功:目录{}", filePath);
        } catch (IOException e) {
            log.error("删除文件失败:{}", filePath, e);
        }
    }

    /**
     * 是否为 UUID
     *
     * @param str UUID字符串
     */
    public boolean isUUID(String str) {
        if (str == null) {
            return false;
        }

        // 去除所有的连字符
        str = str.replace("-", "");

        // 检查长度是否为32（标准UUID去掉连字符后的长度）
        if (str.length() != 32) {
            return false;
        }

        // 检查是否只包含合法的十六进制字符
        try {
            // 尝试将字符串解析为UUID
            UUID.fromString(str.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            ));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}