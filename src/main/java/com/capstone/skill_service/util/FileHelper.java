package com.capstone.skill_service.util;

import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.exception.FileException;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.service.PreSignedUrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;


public class FileHelper {

    @Value("${FILE_MAX_SIZE}")
    static private int maxFileSize=10;
    private FileHelper() {} // prevent initialization
    public static void validateImage(MultipartFile image) {
        long fileSize = DataSize.ofMegabytes(maxFileSize).toBytes();

        if (image.isEmpty()) {
            throw new FileException("File is empty");
        }

        if (image.getSize() > fileSize) {
            throw new FileException("File size exceeds " + maxFileSize + "MB limit");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileException("Only image files are allowed");
        }
    }

    public static void generatePresignedUrl(SkillCapsuleEntity savedCapsule,
                                     CapsuleResponseDto responseDto,
                                     PreSignedUrlService preSignedUrlService ){
        if (savedCapsule.getImage() != null) {
            String presignedUrl = preSignedUrlService.generatePresignedUrl(savedCapsule.getImage());
            responseDto.setImage(presignedUrl);
        }
    }

    public static void generatePresignedUrl(ClusterEntity savedCluster,
                                            ClusterResponseDto responseDto,
                                            PreSignedUrlService preSignedUrlService ){
        if (savedCluster.getImageName() != null) {
            String presignedUrl = preSignedUrlService.generatePresignedUrl(savedCluster.getImageName());
            responseDto.setImageName(presignedUrl);
        }
    }

    public static String getGeneratedPresignedUrl(ClusterEntity savedCluster,
                                            PreSignedUrlService preSignedUrlService ){
        if (savedCluster.getImageName() != null) {
            return preSignedUrlService.generatePresignedUrl(savedCluster.getImageName());
        }
        return null;
    }

    public static String getGeneratedPresignedUrl(SkillCapsuleEntity savedCapsule,
                                            PreSignedUrlService preSignedUrlService ){
        if (savedCapsule.getImage() != null) {
            return preSignedUrlService.generatePresignedUrl(savedCapsule.getImage());
        }
        return null;
    }
}
