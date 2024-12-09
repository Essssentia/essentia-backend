package com.sw.essentiabackend.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 메서드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
            .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        // 변환된 파일을 S3에 업로드
        return upload(uploadFile, dirName);
    }

    // 파일을 S3에 업로드하는 메서드
    private String upload(File uploadFile, String dirName) {
        // UUID를 사용하여 파일 이름이 중복되는 것을 방지
        String uniqueFileName = UUID.randomUUID() + "_" + uploadFile.getName();
        String fileName = dirName + "/" + uniqueFileName;
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info("Uploaded image URL: {}", uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // 파일을 S3에 업로드하고 URL을 반환하는 메서드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
            new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)  // 업로드된 파일에 공용 읽기 권한 부여
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3에서 파일을 삭제하는 메서드
    public void delete(String fileUrl) {
        String path = fileUrl.replaceFirst("https://", "");
        int firstSlashIndex = path.indexOf('/');
        if (firstSlashIndex == -1) {
            log.error("Invalid file URL format: {}", fileUrl);
            return;
        }
        String host = path.substring(0, firstSlashIndex);  // 호스트 부분 추출
        String filePath = path.substring(firstSlashIndex + 1);  // 파일 경로 부분 추출
        String bucketName = host.split("\\.")[0];
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, filePath));
            log.info("S3에서 파일 삭제: {}", filePath);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", fileUrl, e);
        }
    }

    // S3에 파일 업로드 후 로컬에 생성된 임시 파일을 삭제하는 메서드
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("로컬 파일이 삭제되었습니다.");
        } else {
            log.info("로컬 파일 삭제 실패.");
        }
    }

    // MultipartFile을 File로 변환하는 메서드
    private Optional<File> convert(MultipartFile file) throws IOException {
        // MultipartFile에서 원본 파일 이름을 가져와 File 객체 생성
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        if (convertFile.createNewFile()) {
            // 파일 출력 스트림을 사용하여 파일에 데이터를 씀
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}