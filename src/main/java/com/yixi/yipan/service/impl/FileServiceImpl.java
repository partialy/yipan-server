package com.yixi.yipan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.yipan.dto.FileDTO;
import com.yixi.yipan.entity.FileInfo;
import com.yixi.yipan.entity.User;
import com.yixi.yipan.exception.BusinessException;
import com.yixi.yipan.mapper.FileMapper;
import com.yixi.yipan.mapper.UserMapper;
import com.yixi.yipan.param.file.*;
import com.yixi.yipan.service.FileService;
import com.yixi.yipan.common.utils.StorageUtils;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;

import jakarta.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 文件服务实现类
 *
 * @author yixi
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    @Resource
    private StorageUtils storageUtils;
    @Autowired
    private UserMapper userMapper;

    @Value("${file.upload.temp-path}")
    private String uploadTempPath;

    @Value("${file.path}")
    private String uploadPath;

    private static final Logger LOGGER = Logger.getLogger(FileServiceImpl.class.getName());

    // 分页查询文件
    @Override
    public IPage<FileDTO> queryByPage(FileQueryParam param, long current, long size) {
        Page<FileInfo> page = new Page<>(current, size);
        // 查询文件信息
        Page<FileInfo> filePage = baseMapper.selectPage(page, null);
        // 转换为DTO
        Page<FileDTO> dtoPage = new Page<>(filePage.getCurrent(), filePage.getSize(), filePage.getRecords().size());
        dtoPage.setRecords(filePage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        return dtoPage;
    }

    // 获取所有文件
    @Override
    public List<FileDTO> selectAllByUserId(String userId) {
        return baseMapper.selectAllByUserId(userId);
    }

    //    批量复制文件
    @Override
    public String batchCopyFiles(List<String> ids, String targetFolderId) {
        try {
            for (String id : ids) {
                // 复制文件
                FileServiceImpl.this.copyFile(id, targetFolderId);
            }
            return "success";
        } catch (Exception e) {
            log.error("batchCopyFiles error", e);
            return "error";
        }
    }

    // 获取文件信息
    @Override
    public FileDTO getFileById(String id) {
        FileInfo fileInfo = baseMapper.selectById(id);
        return convertToDTO(fileInfo);
    }

    // 上传文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDTO uploadFile(MultipartFile file, String parentId, String userId) {
        // 文件信息
        FileInfo fileInfo = new FileInfo();
        // 文件名
        fileInfo.setFileName(file.getOriginalFilename());
        LOGGER.info("uploadFile fileName: {}" + fileInfo.getFileName());
        LOGGER.info(baseMapper.selectByFileNameAndParentIdAndUserId(fileInfo.getFileName(),parentId,userId).toString());
        if(baseMapper.selectByFileNameAndParentIdAndUserId(fileInfo.getFileName(),parentId,userId)!=null)
        {
            handleFileName(fileInfo);
        }
        //md5
        fileInfo.setMd5(storageUtils.getFileMd5(file));
        // id
        fileInfo.setId(UUID.randomUUID().toString());

        fileInfo.setFileType(file.getContentType());
        fileInfo.setParentId(parentId);
        fileInfo.setUserId(userId);
        // 文件大小
        fileInfo.setFileSize(file.getSize());
        // 判断空间是否足够，更新用户的存储空间
        if(!this.checkStorageSpace(userId, file.getSize())){
            throw new RuntimeException("存储空间不足");
        }
        // 创建时间
        fileInfo.setCreatedAt(new Date());
        // 更新时间
        fileInfo.setUpdatedAt(new Date());
        // 删除时间
        fileInfo.setDeletedAt(null);
        // 设置文件类型为文件
        fileInfo.setIsFolder(0);
        // 路径
        fileInfo.setFilePath(storageUtils.generateFilePath(fileInfo));
        // 保存文件
        storageUtils.saveFile(file,fileInfo);
        return convertToDTO(fileInfo);
    }

    // 上传文件分片
    @Override
    public boolean uploadChunk(FileChunkParam param, MultipartFile file) {
        try {
            // 创建分片存储的临时目录
            String chunkFolderPath = uploadTempPath + File.separator + param.getFileMd5();
            Path chunkFolder = Paths.get(chunkFolderPath);
            if (!Files.exists(chunkFolder)) {
                Files.createDirectories(chunkFolder);
            }
            // 保存分片文件
            String chunkPath = chunkFolderPath + File.separator + param.getChunkNumber();
            File chunkFile = new File(chunkPath);
            file.transferTo(chunkFile);
            return true;
        }catch (Exception e){
            log.error("uploadChunk error", e);
            return false;
        }
    }

    // 合并文件分片
    @Override
    public FileDTO mergeChunks(FileChunkMergeParam param) {
        String chunkFolderPath = uploadTempPath  + param.getFileMd5();
        String mergedFilePath = uploadPath  + param.getFileName();

        File mergedFile = new File(mergedFilePath);
        try {
            // 检查所有分片是否存在
            if (!checkChunksExists(chunkFolderPath, param.getTotalChunks())) {
                throw new BusinessException("部分分片文件缺失，请重新上传");
            }

            // 合并分片
            mergeFiles(chunkFolderPath, mergedFile, param.getTotalChunks());

            // 验证合并后的文件MD5
            String mergedFileMd5 = getFileMd5(mergedFile);
            if (!mergedFileMd5.equals(param.getFileMd5())) {
                throw new BusinessException("文件MD5校验失败，请重新上传");
            }

            // 保存文件
            FileInfo fileInfo = new FileInfo();
            // id
            fileInfo.setId(UUID.randomUUID().toString());
            // 文件名
            fileInfo.setFileName(param.getFileName());
            //size
            fileInfo.setFileSize(mergedFile.length());
            //md5
            fileInfo.setMd5(mergedFileMd5);
            // 父文件夹ID

            fileInfo.setParentId(param.getParentId());
            // 用户ID
            fileInfo.setUserId(param.getUserId());
            // 创建时间
            fileInfo.setFileType(getFileType(param.getFileName()));
            // 时间
            fileInfo.setDeletedAt(null);
            fileInfo.setCreatedAt(new Date());
            fileInfo.setUpdatedAt(new Date());
            // 设置文件类型为文件
            fileInfo.setIsFolder(0);
            // 路径
            fileInfo.setFilePath(storageUtils.generateFilePath(fileInfo));
            // 更新用户的存储空间
            userMapper.updateStorageUsed(fileInfo.getUserId(), fileInfo.getFileSize());
            // 保存文件
            storageUtils.saveFile(storageUtils.convertFileToMultipartFile(mergedFile),fileInfo);
            // 删除分片文件
            FileUtils.deleteDirectory(new File(chunkFolderPath));
            // 删除合并后的文件
            FileUtils.delete(mergedFile);

            return convertToDTO(fileInfo);
        }catch (Exception e){
            log.error("mergeChunks error", e);
            throw new BusinessException("合并文件分片失败");
        }
    }

    // 下载文件
    @Override
    public byte[] downloadFile(String filePath) {
        System.out.println("serviceimpl"+filePath);
        return storageUtils.downloadFile(filePath);
    }

    // 创建文件夹
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDTO createFolder(FileFoldCreateParam param) {
        FileInfo fileInfo = new FileInfo();
        BeanUtils.copyProperties(param, fileInfo);
        fileInfo.setFileName(param.getFileName());
        // id
        fileInfo.setId(UUID.randomUUID().toString());
        // 父文件夹ID
        if(!storageUtils.isUUID(param.getParentId())) {
            fileInfo.setParentId(null);
        }else {
            fileInfo.setParentId(param.getParentId());
        }
        // 用户ID
        fileInfo.setUserId(param.getUserId());
        // 创建时间
        fileInfo.setCreatedAt(new Date());
        // 更新时间
        fileInfo.setUpdatedAt(new Date());
        fileInfo.setFileSize(0L);
        fileInfo.setDeletedAt(null);
        // 设置文件类型为文件夹
        fileInfo.setIsFolder(1);
        // 路径
        fileInfo.setFilePath(param.getPath());
        // 保存文件夹信息到数据库
        baseMapper.insert(fileInfo);
        return convertToDTO(fileInfo);
    }
   // 查询文件夹下的所有文件
    @Override
    public List<FileDTO> queryByParentId(String parentId) {
        return baseMapper.selectByParentId(parentId);
    }

    // 更新文件名,还原文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDTO updateFile(String id, FileUpdateParam param, boolean isRestore) {
        FileInfo fileInfo = baseMapper.selectById(id);
        if(isRestore){
            // 恢复文件
            baseMapper.Restore(id);
            return convertToDTO(fileInfo);
        }
        fileInfo.setFileName(param.getFilename());
        fileInfo.setUpdatedAt(new Date());
        baseMapper.updateById(fileInfo);
        return convertToDTO(fileInfo);
    }

    // 查询用户根目录的所有文件
    @Override
    public List<FileDTO> selectByUserIdAndParentIdIsRoot(String userId) {
        return baseMapper.selectByUserIdAndParentIdIsRoot(userId);
    }

    // 删除文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String id) {
        FileInfo fileInfo = baseMapper.selectById(id);
        Long fileSize = fileInfo.getFileSize();
        if(fileInfo.getIsFolder() == 1){
            fileInfo.setFileSize(0L);
            baseMapper.updateById(fileInfo);
        }
        if(fileInfo.getDeletedAt() != null){
            String filePath = fileInfo.getFilePath();
            if(baseMapper.selectByMd5(fileInfo.getMd5()).size() == 1){
                // 物理删除
                storageUtils.deleteFileInHarDisk(filePath);
            }
            baseMapper.deleteByIdReal(id);
            // 更新用户的存储空间
            userMapper.updateStorageUsed(fileInfo.getUserId(), -fileSize);
            return;
        }
        // 放入回收站，不需要更新用户的存储空间
        baseMapper.deleteById(id);
//        查找子文件
        List<FileDTO> fileDTOs = baseMapper.selectByParentId(fileInfo.getId());
        // 删除子文件
        for (FileDTO fileDTO : fileDTOs) {
            deleteFile(fileDTO.getId());
        }
    }

    // 批量删除文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteFiles(List<String> ids) {
        for (String id : ids) {
            deleteFile(id);
        }
    }

    // 移动文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDTO moveFile(List<String> fileIds, String targetFolderId) {
        for (String fileId : fileIds) {
            // 查询文件信息
            FileInfo fileInfo = baseMapper.selectById(fileId);
            // 移动到目标文件夹
            fileInfo.setParentId(targetFolderId);
            fileInfo.setUpdatedAt(new Date());
            baseMapper.updateById(fileInfo);
        }
        return null;
    }

    // 复制文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyFile(String id, String targetFolderId) {
        // 查询文件信息
        FileInfo fileInfo = baseMapper.selectById(id);
        if(targetFolderId.isEmpty()) {
            //复制到根目录
            fileInfo.setParentId(null);
            this.modifyFileInfo(fileInfo, targetFolderId);
            return baseMapper.selectById(fileInfo.getParentId()).getFileName();
        }
        if(storageUtils.isUUID(targetFolderId)) {
            if(targetFolderId.equals(fileInfo.getParentId())){
//                复制到同一目录
                fileInfo.setParentId(null);
                handleFileName(fileInfo);
                this.modifyFileInfo(fileInfo, targetFolderId);
                return baseMapper.selectById(fileInfo.getParentId()).getFileName();
            }
//            复制到其他目录
            fileInfo.setParentId(targetFolderId);
            this.modifyFileInfo(fileInfo, targetFolderId);
            return baseMapper.selectById(fileInfo.getParentId()).getFileName();
        }
        return "error";
    }


    // 获取下载链接
    @Override
    public String getDownloadUrl(String fileId) {
        return storageUtils.getDownloadUrl(fileId);
    }

    // 检查存储空间
    @Override
    public boolean checkStorageSpace(String userId, Long fileSize) {
        Long totalSpace = userMapper.selectById(userId).getStorageLimit();
        Long usedSpace = userMapper.selectById(userId).getStorageUsed();
        if(usedSpace + fileSize <= totalSpace){
            // 更新用户的存储空间
            userMapper.updateStorageUsed(userId, fileSize);
            return true;
        }
        return false;
    }

    // 检查路径有效性
    @Override
    public boolean isValidPath(String fileId, String targetFolderId) {
        // TODO: 实现路径有效性检查逻辑
        return false;
    }

    @Override
    public boolean ifFastUploadFile(String fileMD5, String parentId,String userId){
        FileInfo fileInfo = baseMapper.queryByMd5(fileMD5);
        User userInfo = userMapper.selectById(userId);
        if(fileInfo != null){
            // 文件已存在
            // 更新文件信息，生成新的uuid，准备插入数据库
            fileInfo.setId(UUID.randomUUID().toString());
            fileInfo.setUpdatedAt(new Date());
            fileInfo.setUserId(userId);

            if(storageUtils.isUUID(parentId)){
                if(fileInfo.getParentId()==null){
                    fileInfo.setParentId(parentId);
                    baseMapper.insert(fileInfo);
                    userInfo.setStorageUsed(userInfo.getStorageUsed() + fileInfo.getFileSize());
                    userMapper.updateById(userInfo);
                    return true;
                }
                if(fileInfo.getParentId().equals(parentId)){
                    // 同一文件夹下存在相同文件，更新文件信息
                    handleFileName(fileInfo);
                    baseMapper.insert(fileInfo);
                    userInfo.setStorageUsed(userInfo.getStorageUsed() + fileInfo.getFileSize());
                    userMapper.updateById(userInfo);
                    return true;
                }
                fileInfo.setParentId(null);
                userInfo.setStorageUsed(userInfo.getStorageUsed() + fileInfo.getFileSize());
                userMapper.updateById(userInfo);
                baseMapper.insert(fileInfo);
                return true;
            }
            //根目录下存在相同文件
            handleFileName(fileInfo);
            fileInfo.setParentId(null);
            userInfo.setStorageUsed(userInfo.getStorageUsed() + fileInfo.getFileSize());
            userMapper.updateById(userInfo);
            baseMapper.insert(fileInfo);
            return true;
        }
        return false;
    }

    // 回收站
    @Override
    public List<FileDTO> recycleBin(String userId) {
        return baseMapper.queryRecycleBin(userId);
    }

    // 检查文件访问权限
    private void checkFileAccess(FileInfo fileInfo) {
        // TODO: 实现文件访问权限检查逻辑
    }

    // 检查文件名是否重复
    private boolean checkNameExists(String filename, String parentId) {
        // TODO: 实现文件名重复检查逻辑
        return false;
    }

    // 转换为DTO
    private FileDTO convertToDTO(FileInfo fileInfo) {
        FileDTO dto = new FileDTO();
        BeanUtils.copyProperties(fileInfo, dto);
        return dto;
    }

    private String getFileMd5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(fis);
        }
    }

    private String getFileType(String filename) {
        return FilenameUtils.getExtension(filename).toLowerCase();
    }


    private boolean checkChunksExists(String chunkFolderPath, int totalChunks) {
        for (int i = 0; i < totalChunks; i++) {
            File chunk = new File(chunkFolderPath + File.separator + i);
            if (!chunk.exists()) {
                return false;
            }
        }
        return true;
    }

    private void mergeFiles(String chunkFolderPath, File mergedFile, int totalChunks) throws IOException {
        try (FileChannel outChannel = new FileOutputStream(mergedFile).getChannel()) {
            for (int i = 0; i < totalChunks; i++) {
                File chunk = new File(chunkFolderPath + File.separator + i);
                try (FileChannel inChannel = new FileInputStream(chunk).getChannel()) {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
            }
        }
    }

    public void modifyFileInfo(FileInfo fileInfo, String targetFolderId) {
        List<FileInfo> exisitFile = baseMapper.selectByMd5AndParentId(fileInfo.getMd5(),targetFolderId);
        if(exisitFile.size() > 1 ){
            for (FileInfo file : exisitFile) {
                if (file.getFileName().equals(fileInfo.getFileName())) {
                    handleFileName(fileInfo);
                }
            }
        }else{
            if(exisitFile.size() == 1) {
                if (exisitFile.get(0).getFileName().equals(fileInfo.getFileName())) {
                    handleFileName(fileInfo);
                }
            }
        }
        userMapper.updateStorageUsed(fileInfo.getUserId(), fileInfo.getFileSize());
        fileInfo.setParentId(targetFolderId);
        fileInfo.setId(UUID.randomUUID().toString());
        fileInfo.setCreatedAt(new Date());
        fileInfo.setUpdatedAt(new Date());
        baseMapper.insert(fileInfo);
    }

    public void handleFileName(FileInfo fileInfo){
        String fileName = fileInfo.getFileName();
        String randomStr = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 3)
                .toUpperCase();

        // 处理文件名
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            // 有扩展名的情况
            String nameWithoutExt = fileName.substring(0, lastDotIndex);
            String extension = fileName.substring(lastDotIndex);
            fileInfo.setFileName(nameWithoutExt + "_" + randomStr + extension);
        } else {
            // 没有扩展名的情况
            fileInfo.setFileName(fileName + "_" + randomStr);
        }
    }
}