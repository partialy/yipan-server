package com.yixi.yipan.param.file;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileChunkMergeParam {
    @NotNull(message = "文件MD5不能为空")
    private String fileMd5;

    @NotNull(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

    private String parentId;

    private String userId;
}
