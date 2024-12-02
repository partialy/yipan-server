package com.yixi.yipan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.entity.FileInfo;
import com.yixi.yipan.param.file.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口
 *
 * @author yixi
 */
public interface FileService extends IService<FileInfo> {

    /**
     * 分页查询文件列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页结果
     */
    IPage<FileDTO> queryByPage(long current, long size, String sortBy, String order);

    /**
     * 获取文件详情
     *
     * @param id 文件ID
     * @return 文件详情
     */
    FileDTO getFileById(String id);

    /**
     * 上传文件
     *
     * @param file     文件数据
     * @param parentId 父文件夹ID
     * @return 文件信息
     */
    FileDTO uploadFile(MultipartFile file, String parentId, String userId);

    /**
     * 秒传文件
     *
     * @param fileMD5  文件数据
     * @param parentId 父文件夹ID
     * @param userId   用户ID
     * @return true-秒传成功，false-秒传失败
     */
    boolean ifFastUploadFile(String fileName, String fileMD5, String parentId, String userId);

    /**
     * 上传文件分片
     *
     * @param file  文件数据
     * @param param 分片参数
     * @return 文件信息
     */
    boolean uploadChunk(FileChunkParam param, MultipartFile file);

    /**
     * 合并文件分片
     *
     * @param param 合并参数
     * @return 文件信息
     */
    FileDTO mergeChunks(FileChunkMergeParam param);

    /**
     * 创建文件夹
     *
     * @param param 创建参数
     * @return 文件夹信息
     */
    FileDTO createFolder(FileFoldCreateParam param);

    /**
     * 查询文件夹下的所有文件
     *
     * @param parentId 父文件夹ID
     * @return 文件列表
     */
    List<FileDTO> queryByParentId(String parentId);

    /**
     * 查询根目录下的文件
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileDTO> selectByUserIdAndParentIdIsRoot(String userId);

    /**
     * 更新文件信息
     *
     * @param id    文件ID
     * @param param 更新参数
     * @return 更新后的文件信息
     */
    FileDTO updateFile(String id, FileUpdateParam param, boolean isRestore);

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    void deleteFile(String id);

    /**
     * 批量删除文件
     *
     * @param ids 文件ID列表
     */
    void batchDeleteFiles(List<String> ids);

    /**
     * 移动文件
     *
     * @param fileIds         文件ID
     * @param targetFolderId 目标文件夹ID
     * @return 更新后的文件信息
     */
    FileDTO moveFile(List<String> fileIds, String targetFolderId);

    /**
     * 复制文件
     *
     * @param id             文件ID
     * @param targetFolderId 目标文件夹ID
     * @return 更新后的文件信息
     */
    String copyFile(String id, String targetFolderId);

    /**
     * 获取文件下载链接
     *
     * @param id 文件ID
     * @return 下载链接
     */
    String getDownloadUrl(String id);

    /**
     * 检查存储空间是否足够
     *
     * @param userId   用户ID
     * @param fileSize 文件大小
     * @return true-空间足够，false-空间不足
     */
    boolean checkStorageSpace(String userId, Long fileSize);

    /**
     * 检查文件夹路径是否有效
     *
     * @param fileId         文件ID
     * @param targetFolderId 目标文件夹ID
     * @return true-有效，false-无效
     */
    boolean isValidPath(String fileId, String targetFolderId);

    /**
     * 获取回收站文件列表
     *
     * @param userId 用户ID
     * @return 回收站文件列表
     */
    List<FileDTO> recycleBin(String userId);

    /**
     * 获取所有文件列表
     *
     * @param userId 用户ID
     * @return 所有文件列表
     */
    List<FileDTO> selectAllByUserId(String userId);

    /**
     * 批量复制文件
     *
     * @param ids            文件ID列表
     * @param targetFolderId 目标文件夹ID
     * @return 提示信息
     */
    String batchCopyFiles(List<String> ids, String targetFolderId);

    /**
     * 获取文件类型分布
     * @return 类型和对应数量
     */
    List<Map<String, Object>> getFileTypeVO();

    /**
     * 获取多个文件
     */
    List<FileDTO> getFilesByIds(List<String> ids);
}