package com.tanuj.EcommerceApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class AwsS3Service {

    private final String bucketName = "tanuj-ecommerce-bucket";

    @Value("${aws.s3.access}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secretKey}")
    private String awsS3SecretKey;

    public String saveImageToS3(MultipartFile photo){
        try {
            String s3FileName = photo.getOriginalFilename();
            long contentLength = photo.getSize();

            //create aws creds using access and secret key
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsS3AccessKey,awsS3SecretKey);

            //create S3 client with config, creds and region
            try (S3Client s3Client = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.AP_SOUTH_1)
                    .build()) {

                //get input stream from photo
                InputStream inputStream = photo.getInputStream();

                //create a put request to upload the image to s3
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .contentType("image/jpeg")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            }

            return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + s3FileName;

        }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Unable to upload image");
        }
    }
}
