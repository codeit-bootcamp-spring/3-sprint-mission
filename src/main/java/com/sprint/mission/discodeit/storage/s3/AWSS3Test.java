package com.sprint.mission.discodeit.storage.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.storage.s3
 * FileName     : AWSS3Test
 * Author       : dounguk
 * Date         : 2025. 7. 1.
 */

@Service
public class AWSS3Test {
//    private final S3Client s3Client;
//    private final String bucketName;
//    private final String region;
//
//    public AWSS3Test(@Value("${discodeit.storage.s3.access-key}") String accessKey,
//                     @Value("${discodeit.storage.s3.access-key}") String secretKey,
//                     @Value("${discodeit.storage.s3.bucket}") String bucketName,
//                     @Value("${discodeit.storage.s3.region}") String region) {
//
//        this.bucketName = bucketName;
//        this.region = region;
//
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//        this.s3Client = S3Client.builder()
//            .region(Region.of(region))
//            .credentialsProvider(StaticCredentialsProvider.create(credentials))
//            .build();
//    }
//
//    public String upload(MultipartFile file) {
//        try{
//            String fileName = file.getOriginalFilename();
//
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//            return fileName;
//        } catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String getFileUrl(String fileName) {
//        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
//    }
//
//    public String generateFileName(String fileName) {
//        String extension = "";
//        if (fileName != null && fileName.contains(".")) {
//            extension = fileName.substring(fileName.lastIndexOf("."));
//        }
//        return UUID.randomUUID().toString() + extension;
//    }

}
