package com.yixi.yipan.mapper;

import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * <p>
 * 文件信息表 Mapper 接口
 * </p>
 *
 * @author yixi
 * @since 2024-10-31
 */

 @Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
    /**
     * 根据ID查询文件
     */
    FileInfo queryById(String id);

    /*
    * 根据文件名和父目录ID查询文件
    */
    List<FileInfo> selectByFileNameAndParentIdAndUserId(String fileName, String parentId, String userId);

    /**
     * 查询指定用户的文件列表
     */
    List<FileInfo> queryByUserId(@Param("userId") String userId, @Param("parentId") Long parentId);

    /**
     * 插入文件记录
     */
    int insert(FileInfo fileInfo);

    /**
     * 更新文件信息
     */
    int update(FileInfo fileInfo);

    /**
     * 软删除文件
     */
    int deleteById(String id);

    /**
     * 硬删除文件
     */
    int deleteByIdReal(String md5);

    /**
     * 根据MD5查询文件
     */
    FileInfo queryByMd5(String md5);

    /**
     * 根据MD5查询文件列表
     */
    List<FileInfo> selectByMd5(String md5);

    List<FileInfo> selectByMd5AndParentId(String md5, @Param("parentId") String parentId);
    /**
     * 根据父目录ID查询文件列表
     */
    List<FileDTO> selectByParentId(String folderId);

    /**
     * 根据用户ID和父目录ID查询文件列表
     */

    List<FileDTO> selectByUserIdAndParentIdIsRoot(String userId);
    /**
     * 回收站查询文件列表
     */

    List<FileDTO> queryRecycleBin(String userId);

    /**
     * 恢复文件
     */
    void Restore(String id, String fileName);

    /**
     * 所有文件查询
     */
    List<FileDTO> selectAllByUserId(String userId);

    /**
     * 获取文件类型统计信息
     */
    List<Map<String, Object>> selectFileTypeStatistics();
}
