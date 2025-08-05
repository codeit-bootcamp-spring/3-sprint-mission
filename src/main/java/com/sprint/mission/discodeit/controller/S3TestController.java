package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.storage.s3.AWSS3Test;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3TestController {

    private final AWSS3Test awsS3Test;

    @GetMapping("/test/s3/all")
    public ResponseEntity<String> testAll() {
        try {
            awsS3Test.runAllTests();
            return ResponseEntity.ok("S3 전체 테스트(텍스트+이미지) 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("S3 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/upload")
    public ResponseEntity<String> testUpload() {
        try {
            awsS3Test.testUpload();
            return ResponseEntity.ok("텍스트 업로드 테스트 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("텍스트 업로드 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/download")
    public ResponseEntity<String> testDownload() {
        try {
            awsS3Test.testDownload();
            return ResponseEntity.ok("텍스트 다운로드 테스트 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("텍스트 다운로드 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/presigned")
    public ResponseEntity<String> testPresigned() {
        try {
            awsS3Test.testPresignedUrl();
            return ResponseEntity.ok("텍스트 PresignedURL 테스트 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("PresignedURL 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/image/all")
    public ResponseEntity<String> testImageAll() {
        try {
            awsS3Test.runImageTests();
            return ResponseEntity.ok("이미지 전체 테스트 완료! 콘솔 로그에서 PresignedURL을 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("이미지 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/image/upload")
    public ResponseEntity<String> testImageUpload() {
        try {
            awsS3Test.testImageUpload();
            return ResponseEntity.ok("이미지 업로드 테스트 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("이미지 업로드 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/image/download")
    public ResponseEntity<String> testImageDownload() {
        try {
            awsS3Test.testImageDownload();
            return ResponseEntity.ok("이미지 다운로드 테스트 완료! 콘솔 로그를 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("이미지 다운로드 테스트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test/s3/image/presigned")
    public ResponseEntity<String> testImagePresigned() {
        try {
            awsS3Test.testImagePresignedUrl();
            return ResponseEntity.ok("이미지 PresignedURL 생성 완료! 콘솔 로그에서 URL을 복사해서 브라우저에서 열어보세요!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("이미지 PresignedURL 생성 실패: " + e.getMessage());
        }
    }

}

