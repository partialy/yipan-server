package com.yixi.yipan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.param.file.*;
import com.yixi.yipan.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * 文件管理接口
 *
 * @author yixi
 */
@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(summary = "分页查询文件列表")
    @GetMapping
    public ResponseEntity<IPage<FileDTO>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向(ascending/descending)") @RequestParam(required = false) String order) {
        return ResponseEntity.ok(fileService.queryByPage(current, size, sortBy, order));
    }

    @Operation(summary = "获取文件列表")
    @GetMapping("/folder/list")
    public ResponseEntity<List<FileDTO>> listAll(
            @Valid FileQueryParam param) {
        return ResponseEntity.ok(fileService.queryByParentId(param.getParentId()));
    }

    @Operation(summary = "获取根目录")
    @GetMapping("/rootList")
    public ResponseEntity<List<FileDTO>> listRoot(@Param(value = "userId") String userId) {
        return ResponseEntity.ok(fileService.selectByUserIdAndParentIdIsRoot(userId));
    }

    @Operation(summary = "获取所有文件")
    @GetMapping("/allFiles")
    public ResponseEntity<List<FileDTO>> allFiles(@Param(value = "userId") String userId) {
        return ResponseEntity.ok(fileService.selectAllByUserId(userId));
    }

    @Operation(summary = "获取文件详情")
    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(fileService.getFileById(id));
    }

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public ResponseEntity<FileDTO> upload(
            @RequestParam MultipartFile file, String parentId, String userId) {
        return ResponseEntity.ok(fileService.uploadFile(file, parentId, userId));
    }

    @Operation(summary = "下载文件")
    @CrossOrigin(
            originPatterns = "*",
            allowedHeaders = "*",
            exposedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        FileDTO fileDTO = fileService.getFileById(id);
        File file = new File(fileDTO.getFilePath());

        // 设置文件下载的响应头
        String fileName = fileDTO.getFileName();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentLengthLong(file.length());

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream())) {

            byte[] buffer = new byte[8192]; // 8KB缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            if (!e.getMessage().contains("管道已断开") &&
                    !e.getMessage().contains("连接被对端重置")) {
                log.error("文件传输错误: {}", fileName, e);
                throw new RuntimeException("文件下载失败", e);
            }
        }
    }

    @Operation(summary = "预览文件")
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        FileDTO fileDTO = fileService.getFileById(id);
        File file = new File(fileDTO.getFilePath());

        // 设置文件类型
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        response.setContentType(contentType);

        // 支持范围请求
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

        long fileLength = file.length();
        long startPos = 0;
        long endPos = fileLength - 1;

        // 处理 Range 请求头
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            try {
                // 解析 Range 头，格式如 "bytes=0-1024" 或 "bytes=1024-"
                String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
                startPos = Long.parseLong(ranges[0]);

                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    endPos = Long.parseLong(ranges[1]);
                }

                if (startPos >= fileLength) {
                    // 请求范围超出文件大小
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
                    return;
                }

                // 确保 endPos 不超过文件大小
                endPos = Math.min(endPos, fileLength - 1);

                // 设置 206 Partial Content 状态
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader(HttpHeaders.CONTENT_RANGE,
                        String.format("bytes %d-%d/%d", startPos, endPos, fileLength));

            } catch (NumberFormatException e) {
                // Range 格式错误，返回整个文件
                startPos = 0;
                endPos = fileLength - 1;
            }
        }

        // 计算需要传输的长度
        long contentLength = endPos - startPos + 1;
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

        // 使用 RandomAccessFile 支持范围读取
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(startPos);

            byte[] buffer = new byte[8192];
            long remaining = contentLength;
            int bytesRead;

            try (BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream())) {
                while (remaining > 0 && (bytesRead = randomAccessFile.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    remaining -= bytesRead;

                    // 定期刷新输出流
                    if (remaining % (buffer.length * 8) == 0) {
                        outputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            // 忽略客户端断开连接的常见异常
            if (!e.getMessage().contains("Broken pipe") &&
                    !e.getMessage().contains("Connection reset") &&
                    !e.getMessage().contains("管道已断开") &&
                    !e.getMessage().contains("连接被对端重置")) {
                log.error("文件预览错误: {}", fileDTO.getFileName(), e);
                throw new RuntimeException("文件预览失败", e);
            }
        }
    }


    @Operation(summary = "创建文件夹")
    @PostMapping("/createFolder")
    public ResponseEntity<FileDTO> createFolder(@Valid @RequestBody FileFoldCreateParam param) {
        return ResponseEntity.ok(fileService.createFolder(param));
    }

    @Operation(summary = "更新文件信息")
    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> update(
            @PathVariable String id,
            @Valid @RequestBody FileUpdateParam param) {
        return ResponseEntity.ok(fileService.updateFile(id, param, false));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<String> ids) {
        fileService.batchDeleteFiles(ids);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "移动文件")
    @PostMapping("/move")
    public ResponseEntity<FileDTO> moveFile(
            @RequestBody List<String> fileIds,
            @RequestParam String targetFolderId) {
        return ResponseEntity.ok(fileService.moveFile(fileIds, targetFolderId));
    }

    @Operation(summary = "复制文件")
    @PostMapping("/{id}/copy")
    public ResponseEntity<String> copyFile(
            @PathVariable String id,
            @RequestParam String targetFolderId) {
        return ResponseEntity.ok(fileService.copyFile(id, targetFolderId));
    }

    @Operation(summary = "批量复制文件")
    @PostMapping("/batch/copy")
    public ResponseEntity<String> batchCopy(
            @RequestBody List<String> ids,
            @RequestParam String targetFolderId) {
        return ResponseEntity.ok(fileService.batchCopyFiles(ids, targetFolderId));
    }

    @Operation(summary = "批量移动文件")
    @PostMapping("/batch/move")
    public ResponseEntity<FileDTO> batchMove(
            @RequestBody List<String> ids,
            @RequestParam String targetFolderId) {
        return ResponseEntity.ok(fileService.moveFile(ids, targetFolderId));
    }

    @Operation(summary = "获取下载链接")
    @GetMapping("/{id}/getDownloadUrl")
    public ResponseEntity<String> getDownloadUrl(@PathVariable String id) {
        return ResponseEntity.ok(fileService.getDownloadUrl(id));
    }

    @Operation(summary = "上传文件分片")
    @PostMapping("/chunk")
    public ResponseEntity<Boolean> uploadChunk(
            @Valid FileChunkParam param,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadChunk(param, file));
    }

    @Operation(summary = "合并文件分片")
    @PostMapping("/chunk/merge")
    public ResponseEntity<FileDTO> mergeChunks(
            @Valid @RequestBody FileChunkMergeParam param) {
        return ResponseEntity.ok(fileService.mergeChunks(param));
    }

    @Operation(summary = "是否可以秒传")
    @GetMapping("/upload/ifFastUploadFile")
    public ResponseEntity<Boolean> uploadIfFastUploadFile(
            @Param(value = "fileName") String fileName,
            @Param(value = "fileMd5") String fileMd5,
            @Param(value = "parentId") String parentId,
            @Param(value = "userId") String userId
    ) {
        return ResponseEntity.ok(fileService.ifFastUploadFile(fileName, fileMd5, parentId, userId));
    }

    @Operation(summary = "回收站文件列表")
    @GetMapping("/{userId}/recycleBin")
    public ResponseEntity<List<FileDTO>> recycleBin(@PathVariable String userId) {
        return ResponseEntity.ok(fileService.recycleBin(userId));
    }

    @Operation(summary = "还原文件")
    @PostMapping("/restoreFiles")
    public ResponseEntity<Boolean> restoreFiles(@RequestBody List<String> fileIds) {
        for (String fileId : fileIds) {
            fileService.updateFile(fileId, new FileUpdateParam(), true);
        }
        return ResponseEntity.ok(true);
    }
}