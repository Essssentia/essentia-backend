package com.sw.essentiabackend.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.region.static}")
    private String region;  // 애플리케이션 설정 파일에서 AWS 리전(region) 값을 주입받음

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;  // AWS 액세스 키를 설정 파일에서 주입받음, AWS API 호출을 인증하는 데 사용

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Bean  // 이 메서드가 반환하는 객체(AmazonS3)가 스프링 컨텍스트에서 관리되는 빈(Bean)으로 등록
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        // AmazonS3ClientBuilder를 사용하여 AmazonS3 클라이언트를 생성
        // withCredentials 메서드를 사용하여 인증 정보를 설정하고, withRegion 메서드를 사용하여 S3 서비스가 사용할 리전을 설정
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))  // 인증 정보 설정
            .withRegion(region)  // 리전 설정
            .build();  // AmazonS3 객체를 생성하여 반환
    }
}