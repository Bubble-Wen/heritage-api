package org.example.springboot.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.BussinessFileUploadConfig;
import org.example.springboot.dto.FileInfoDTO;
import org.example.springboot.dto.FileUploadDTO;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.enums.FileBusinessTypeEnum;
import org.example.springboot.enums.FileTypeEnum;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务文件管理服务
 * 专注于完整的业务文件管理功能
 * @author system
 */
@Slf4j
@Service
public class FileService {

    @Resource
    private SysFileInfoMapper fileInfoMapper;

    @Resource
    private BussinessFileValidationService bussinessFileValidationService;

    @Value("${file.upload.path:/files}")
    private String uploadBasePath;


    private final static  String BUSSINESS_FILE_RELATIVE_PATH = "bussiness";



    /**
     * 上传文件并绑定业务对象（支持替换）
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO uploadFile(MultipartFile file, FileUploadDTO uploadDTO, Long uploadUserId, boolean replaceOld) {
        try {
            log.info("开始上传文件: 文件名={}, 业务类型={}, 业务ID={}, 替换模式={}",
                    file.getOriginalFilename(), uploadDTO.getBusinessType(), uploadDTO.getBusinessId(), replaceOld);

            // 1. 业务验证
            bussinessFileValidationService.validateFileUpload(file.getOriginalFilename(), uploadDTO.getBusinessType());

            // 2. 权限验证
            bussinessFileValidationService.validateBusinessPermission(uploadDTO.getBusinessType(),
                    uploadDTO.getBusinessId(), uploadUserId);

            // 3. 如果需要替换，先处理旧文件
            if (replaceOld) {
                handleOldFiles(uploadDTO.getBusinessType(), uploadDTO.getBusinessId(), uploadDTO.getBusinessField());
            }

            // 4. 保存文件到磁盘
            String filePath = FileUtil.saveFile(file, BUSSINESS_FILE_RELATIVE_PATH, FileUtil.parseBussinessFileTypeToFolerName(uploadDTO.getBusinessType()));

            // 5. 保存文件信息到数据库
            SysFileInfo fileInfo = createFileInfo(file, uploadDTO, filePath, uploadUserId);
            fileInfoMapper.insert(fileInfo);

            log.info("文件上传成功: ID={}, 路径={}", fileInfo.getId(), filePath);
            return convertToDTO(fileInfo);

        } catch (Exception e) {
            log.error("文件上传失败: 文件名={}, 错误={}", file.getOriginalFilename(), e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传临时文件
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO uploadTempFile(MultipartFile file, Long uploadUserId) {
        try {
            log.info("开始上传临时文件: 文件名={}", file.getOriginalFilename());

            // 基础验证
            FileUtil.validateBasicFile(file);

            // 保存到临时目录
            String filePath = FileUtil.saveFile(file, "temp",null);

            // 创建临时文件记录
            FileUploadDTO tempDTO = buildTempUploadDTO();
            SysFileInfo fileInfo = createFileInfo(file, tempDTO, filePath, uploadUserId);
            fileInfo.setIsTemp(1);
            fileInfo.setExpireTime(LocalDateTime.now().plusHours(24)); // 24小时后过期

            fileInfoMapper.insert(fileInfo);

            log.info("临时文件上传成功: ID={}, 路径={}", fileInfo.getId(), filePath);
            return convertToDTO(fileInfo);

        } catch (Exception e) {
            log.error("临时文件上传失败: 文件名={}, 错误={}", file.getOriginalFilename(), e.getMessage(), e);
            throw new BusinessException("临时文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 确认临时文件转正式
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO confirmTempFile(Long tempFileId, FileUploadDTO uploadDTO) {
        try {
            log.info("确认临时文件: 文件ID={}, 业务类型={}", tempFileId, uploadDTO.getBusinessType());

            // 查询临时文件
            SysFileInfo tempFile = fileInfoMapper.selectById(tempFileId);
            if (tempFile == null || !tempFile.isTempFile()) {
                throw new BusinessException("临时文件不存在或已失效");
            }

            if (tempFile.isExpired()) {
                throw new BusinessException("临时文件已过期");
            }
            // 业务验证
            bussinessFileValidationService.validateFileUpload(tempFile.getOriginalName(),
                    uploadDTO.getBusinessType());

            // 更新文件信息
            updateTempFileToFormal(tempFile, uploadDTO);
            fileInfoMapper.updateById(tempFile);

            log.info("临时文件确认成功: ID={}", tempFileId);
            return convertToDTO(tempFile);

        } catch (Exception e) {
            log.error("临时文件确认失败: 文件ID={}, 错误={}", tempFileId, e.getMessage(), e);
            throw new BusinessException("临时文件确认失败: " + e.getMessage());
        }
    }

    /**
     * 获取业务对象的文件列表
     */
    public List<FileInfoDTO> getFilesByBusiness(String businessType, String businessId) {
        log.info("查询业务文件: 业务类型={}, 业务ID={}", businessType, businessId);

        LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFileInfo::getBusinessType, businessType)
                .eq(SysFileInfo::getBusinessId, businessId)
                .eq(SysFileInfo::getStatus, 1)
                .orderByDesc(SysFileInfo::getCreateTime);

        List<SysFileInfo> fileList = fileInfoMapper.selectList(queryWrapper);
        return convertToDTOList(fileList);
    }

    /**
     * 获取业务字段的文件
     */
    public List<FileInfoDTO> getFilesByBusinessField(String businessType, String businessId, String businessField) {
        log.info("查询业务字段文件: 业务类型={}, 业务ID={}, 字段={}", businessType, businessId, businessField);

        LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFileInfo::getBusinessType, businessType)
                .eq(SysFileInfo::getBusinessId, businessId)
                .eq(SysFileInfo::getBusinessField, businessField)
                .eq(SysFileInfo::getStatus, 1)
                .orderByDesc(SysFileInfo::getCreateTime);

        List<SysFileInfo> fileList = fileInfoMapper.selectList(queryWrapper);
        return convertToDTOList(fileList);
    }

    /**
     * 删除单个文件
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long userId) {
        try {
            log.info("删除文件: 文件ID={}, 用户ID={}", fileId, userId);

            SysFileInfo fileInfo = fileInfoMapper.selectById(fileId);
            if (fileInfo == null) {
                log.error("文件不存在: 文件ID={}", fileId);
                throw new BusinessException("文件不存在");
            }

            // 权限检查：只能删除自己上传的文件
            // if (!fileInfo.getUploadUserId().equals(userId)) {
            //     throw new BusinessException("无权限删除该文件");
            // }

            // 物理删除数据库记录
            int result = fileInfoMapper.deleteById(fileId);
            if (result <= 0) {
                throw new BusinessException("数据库删除失败");
            }

            // 删除物理文件
            boolean fileDeleted = deletePhysicalFile(fileInfo.getFilePath());
            if (!fileDeleted) {
                log.warn("物理文件删除失败，但数据库记录已删除: 文件路径={}", fileInfo.getFilePath());
            }

            log.info("文件删除成功: 文件ID={}", fileId);
            return true;

        } catch (Exception e) {
            log.error("文件删除失败: 文件ID={}, 错误={}", fileId, e.getMessage(), e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除业务文件
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFilesByBusiness(String businessType, String businessId, String businessField) {
        try {
            log.info("批量删除业务文件: 业务类型={}, 业务ID={}, 字段={}", businessType, businessId, businessField);

            // 查询要删除的文件
            LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysFileInfo::getBusinessType, businessType)
                    .eq(SysFileInfo::getBusinessId, businessId)
                    .eq(SysFileInfo::getStatus, 1);

            if (StrUtil.isNotBlank(businessField)) {
                queryWrapper.eq(SysFileInfo::getBusinessField, businessField);
            }

            List<SysFileInfo> fileList = fileInfoMapper.selectList(queryWrapper);

            if (fileList.isEmpty()) {
                log.info("没有找到需要删除的文件");
                return true;
            }

            // 收集文件ID和路径
            List<Long> fileIds = fileList.stream().map(SysFileInfo::getId).collect(Collectors.toList());
            List<String> filePaths = fileList.stream().map(SysFileInfo::getFilePath).collect(Collectors.toList());

            // 物理删除数据库记录
            int result = fileInfoMapper.deleteByIds(fileIds);

            // 删除物理文件
            int deletedFileCount = 0;
            for (String filePath : filePaths) {
                if (deletePhysicalFile(filePath)) {
                    deletedFileCount++;
                } else {
                    log.warn("物理文件删除失败: {}", filePath);
                }
            }

            log.info("批量删除文件成功: 数据库删除数量={}, 物理文件删除数量={}", result, deletedFileCount);
            return result > 0;

        } catch (Exception e) {
            log.error("批量删除文件失败: 业务类型={}, 错误={}", businessType, e.getMessage(), e);
            throw new BusinessException("批量删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期临时文件
     */
    @Transactional(rollbackFor = Exception.class)
    public int cleanupExpiredTempFiles() {
        try {
            log.info("开始清理过期临时文件");

            LocalDateTime now = LocalDateTime.now();

            // 查询过期的临时文件（创建时间超过24小时且业务类型为temp的文件）
            LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysFileInfo::getBusinessType, "temp")
                    .eq(SysFileInfo::getStatus, 1)  // 有效文件
                    .lt(SysFileInfo::getCreateTime, now.minusHours(24));  // 创建时间超过24小时

            List<SysFileInfo> expiredFiles = fileInfoMapper.selectList(queryWrapper);

            if (expiredFiles.isEmpty()) {
                log.info("没有过期的临时文件需要清理");
                return 0;
            }

            // 收集文件ID和路径
            List<Long> fileIds = expiredFiles.stream().map(SysFileInfo::getId).toList();
            List<String> filePaths = expiredFiles.stream().map(SysFileInfo::getFilePath).toList();

            // 物理删除数据库记录
            int result = fileInfoMapper.deleteByIds(fileIds);

            // 删除物理文件
            int deletedFileCount = 0;
            for (String filePath : filePaths) {
                if (deletePhysicalFile(filePath)) {
                    deletedFileCount++;
                }
            }

            log.info("清理过期临时文件完成: 数据库删除数量={}, 物理文件删除数量={}", result, deletedFileCount);
            return result;

        } catch (Exception e) {
            log.error("清理过期临时文件失败: 错误={}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取文件上传配置
     */
    public BussinessFileUploadConfig getUploadConfig(String businessType) {
        FileBusinessTypeEnum businessEnum = FileBusinessTypeEnum.getByCode(businessType);
        if (businessEnum == null) {
            throw new BusinessException("不支持的业务类型");
        }
        List<String> allowedTypes = Arrays.stream(businessEnum.getAllowedTypes()).toList();
        // 获取允许的文件扩展名
        List<String> allowedExtensions = new ArrayList<>();
        for (String typeCode : businessEnum.getAllowedTypes()) {
            for (FileTypeEnum fileType : FileTypeEnum.values()) {
                if (fileType.getCode().equals(typeCode)) {
                    allowedExtensions.addAll(fileType.getExtensions());
                    break;
                }
            }
        }


        return new BussinessFileUploadConfig(businessEnum.getCode(), businessEnum.getDesc(), allowedTypes, FileUtil.getMaxFileSize(),allowedExtensions);
    }

    // ========== 私有方法 ==========




    /**
     * 创建文件信息对象
     */
    private SysFileInfo createFileInfo(MultipartFile file, FileUploadDTO uploadDTO, String filePath, Long uploadUserId) {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setOriginalName(file.getOriginalFilename());
        fileInfo.setFilePath(filePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(FileTypeEnum.getByFileName(file.getOriginalFilename()).getCode());
        fileInfo.setBusinessType(uploadDTO.getBusinessType());
        fileInfo.setBusinessId(uploadDTO.getBusinessId());
        fileInfo.setBusinessField(uploadDTO.getBusinessField());
        fileInfo.setUploadUserId(uploadUserId);
        fileInfo.setIsTemp(uploadDTO.getIsTemp() != null && uploadDTO.getIsTemp() ? 1 : 0);
        fileInfo.setStatus(1);
        // createTime 由 MyBatis-Plus 自动填充，无需手动设置
        return fileInfo;
    }

    /**
     * 构建临时文件上传DTO
     */
    private FileUploadDTO buildTempUploadDTO() {
        FileUploadDTO tempDTO = new FileUploadDTO();
        tempDTO.setBusinessType("TEMP_FILE");
        tempDTO.setBusinessId("0");
        tempDTO.setIsTemp(true);
        return tempDTO;
    }

    /**
     * 更新临时文件为正式文件
     */
    private void updateTempFileToFormal(SysFileInfo tempFile, FileUploadDTO uploadDTO) {
        tempFile.setBusinessType(uploadDTO.getBusinessType());
        tempFile.setBusinessId(uploadDTO.getBusinessId());
        tempFile.setBusinessField(uploadDTO.getBusinessField());
        tempFile.setIsTemp(0);
        tempFile.setExpireTime(null);
    }

    /**
     * 处理旧文件（替换场景）
     */
    private void handleOldFiles(String businessType, String businessId, String businessField) {
        try {
            log.info("开始处理旧文件: 业务类型={}, 业务ID={}, 字段={}", businessType, businessId, businessField);

            // 查询旧文件
            LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysFileInfo::getBusinessType, businessType)
                    .eq(SysFileInfo::getBusinessId, businessId)
                    .eq(SysFileInfo::getStatus, 1);

            if (StrUtil.isNotBlank(businessField)) {
                queryWrapper.eq(SysFileInfo::getBusinessField, businessField);
            }

            List<SysFileInfo> oldFiles = fileInfoMapper.selectList(queryWrapper);

            if (!oldFiles.isEmpty()) {
                log.info("发现{}个旧文件，准备删除", oldFiles.size());

                // 收集文件ID和路径
                List<Long> fileIds = oldFiles.stream().map(SysFileInfo::getId).collect(Collectors.toList());
                List<String> filePaths = oldFiles.stream().map(SysFileInfo::getFilePath).collect(Collectors.toList());

                // 物理删除数据库记录
                int deletedCount = fileInfoMapper.deleteByIds(fileIds);
                log.info("数据库记录删除完成，删除数量：{}", deletedCount);

                // 删除物理文件
                int deletedFileCount = 0;
                for (String filePath : filePaths) {
                    if (deletePhysicalFile(filePath)) {
                        deletedFileCount++;
                    } else {
                        log.warn("物理文件删除失败: {}", filePath);
                    }
                }

                log.info("旧文件处理完成，数据库删除：{}，物理文件删除：{}", deletedCount, deletedFileCount);
            } else {
                log.info("未发现旧文件，无需处理");
            }
        } catch (Exception e) {
            log.error("处理旧文件失败: 业务类型={}, 错误={}", businessType, e.getMessage(), e);
            // 不抛出异常，允许继续上传新文件
        }
    }

    /**
     * 转换为DTO
     */
    private FileInfoDTO convertToDTO(SysFileInfo fileInfo) {
        FileInfoDTO dto = new FileInfoDTO();
        dto.setId(fileInfo.getId());
        dto.setOriginalName(fileInfo.getOriginalName());
        dto.setFilePath(fileInfo.getFilePath());
        dto.setFileSize(fileInfo.getFileSize());
        dto.setFileType(fileInfo.getFileType());
        dto.setBusinessType(fileInfo.getBusinessType());
        dto.setBusinessId(fileInfo.getBusinessId());
        dto.setBusinessField(fileInfo.getBusinessField());
        dto.setUploadUserId(fileInfo.getUploadUserId());
        dto.setIsTemp(fileInfo.isTempFile());
        dto.setStatus(fileInfo.getStatus());
        dto.setCreateTime(fileInfo.getCreateTime());
        dto.setExpireTime(fileInfo.getExpireTime());
        dto.setFileExtension(fileInfo.getFileExtension());
        dto.setIsExpired(fileInfo.isExpired());

        // 设置描述信息
        for (FileTypeEnum fileType : FileTypeEnum.values()) {
            if (fileType.getCode().equals(fileInfo.getFileType())) {
                dto.setFileTypeDesc(fileType.getDesc());
                break;
            }
        }

        FileBusinessTypeEnum businessType = FileBusinessTypeEnum.getByCode(fileInfo.getBusinessType());
        if (businessType != null) {
            dto.setBusinessTypeDesc(businessType.getDesc());
        }

        return dto;
    }

    /**
     * 转换为DTO列表
     */
    private List<FileInfoDTO> convertToDTOList(List<SysFileInfo> fileList) {
        return fileList.stream().map(this::convertToDTO).toList();
    }

    /**
     * 同步删除物理文件
     */
    private boolean deletePhysicalFile(String filePath) {
        try {
            if (StrUtil.isNotBlank(filePath)) {
                boolean deleted = FileUtil.deleteFile(filePath);
                if (deleted) {
                    log.info("物理文件删除成功: {}", filePath);
                    return true;
                } else {
                    log.warn("物理文件删除失败: {}", filePath);
                    return false;
                }
            }
            return true; // 路径为空认为删除成功
        } catch (Exception e) {
            log.error("删除物理文件异常: 路径={}, 错误={}", filePath, e.getMessage());
            return false;
        }
    }

    /**
     * 异步删除物理文件（保留用于兼容性）
     */
    private void deletePhysicalFileAsync(String filePath) {
        // 现在改为同步删除
        deletePhysicalFile(filePath);
    }
} 
 