package com.yixi.yipan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.entity.FileInfo;
import com.yixi.yipan.param.file.*;
import com.yixi.yipan.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
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

//    @Operation(summary = "下载文件")
//    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
//    @GetMapping("/download/{id}")
//    public ResponseEntity<byte[]> download(@Parameter(description = "文件ID") @PathVariable String id) {
//
//        FileDTO fileDTO = fileService.getFileById(id);
//        String filePath = fileDTO.getFilePath();
//
//         byte[] fileData = fileService.downloadFile(filePath);
//         log.debug("controller download file data: " + Arrays.toString(fileData));
//         if (fileData == null) {
//             return ResponseEntity.notFound().build();
//         }
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//         headers.setContentDisposition(ContentDisposition.attachment()
//                 .filename(FilenameUtils.getName(filePath))
//                 .build());
//
//         return ResponseEntity.ok()
//                 .headers(headers)
//                 .body(fileData);
//    }

    @Operation(summary = "下载文件")
    @CrossOrigin(
            originPatterns = "*",
            allowedHeaders = "*",
            exposedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(
            @Parameter(description = "文件ID") @PathVariable String id,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        try {
            FileDTO fileDTO = fileService.getFileById(id);
            String filePath = fileDTO.getFilePath();
            File file = new File(filePath);

            if (!file.exists()) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

            // 根据文件扩展名设置正确的 Content-Type
            String contentType = Files.probeContentType(file.toPath());
            headers.setContentType(contentType != null ?
                    MediaType.parseMediaType(contentType) :
                    MediaType.APPLICATION_OCTET_STREAM);

            // 处理文件名编码
            String fileName = FilenameUtils.getName(filePath);
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename*=UTF-8''" + encodedFileName);

            final long fileSize = file.length();

            // 处理 Range 请求
            if (rangeHeader != null) {
                try {
                    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                    final long start = Long.parseLong(ranges[0]);
                    final long end = ranges.length > 1 ?
                            Math.min(Long.parseLong(ranges[1]), fileSize - 1) :
                            fileSize - 1;

                    // 验证范围
                    if (start >= fileSize || start > end) {
                        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                                .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                                .build();
                    }

                    final long contentLength = end - start + 1;
                    headers.add(HttpHeaders.CONTENT_RANGE,
                            String.format("bytes %d-%d/%d", start, end, fileSize));
                    headers.setContentLength(contentLength);

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(headers)
                            .body(new RangeResource(file, start, contentLength));

                } catch (NumberFormatException e) {
                    log.warn("Invalid range format: {}", rangeHeader);
                }
            }

            // 非 Range 请求或范围无效时返回完整文件
            headers.setContentLength(fileSize);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new FileSystemResource(file));

        } catch (Exception e) {
            log.error("Error processing file download request for id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 处理范围请求的资源实现
     */
    private static class RangeResource extends AbstractResource {
        private final File file;
        private final long start;
        private final long length;

        public RangeResource(File file, long start, long length) {
            this.file = file;
            this.start = start;
            this.length = length;
        }

        @Override
        public String getDescription() {
            return String.format("Range of file %s from %d with length %d",
                    file.getName(), start, length);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(start);

            return new BufferedInputStream(new FileInputStream(raf.getFD()) {
                private long bytesRead = 0;

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (bytesRead >= length) {
                        return -1;
                    }

                    long remaining = length - bytesRead;
                    int readLen = (int) Math.min(len, remaining);
                    int read = super.read(b, off, readLen);

                    if (read > 0) {
                        bytesRead += read;
                    }
                    return read;
                }

                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        raf.close();
                    }
                }
            });
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public long contentLength() {
            return length;
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
        return ResponseEntity.ok(fileService.updateFile(id, param,false));
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
    @GetMapping("/{id}/download")
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
            @Param(value = "fileMd5") String fileMd5,
            @Param(value = "parentId") String parentId,
            @Param(value = "userId") String userId
    ){
        return ResponseEntity.ok(fileService.ifFastUploadFile(fileMd5, parentId, userId));
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