package com.yixi.yipan.param.file;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileChunkParam {
    @NotNull(message = "文件MD5不能为空")
    private String fileMd5;

    @NotNull(message = "分片序号不能为空")
    private Integer chunkNumber;

    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

    private String fileName;

    private String parentId;
}
