package com.project.market.infra.fileupload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Slf4j
@Service
@Profile("dev")
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected void handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.info("handleMaxUploadSizeExceededException S3bucket 용량을 초과했습니다.", e);
    }

    private final AmazonS3Client client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.root-dir}")
    private String rootDir;

    public String uploadFile(String dir, MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();

        String filename = multipartFile.getOriginalFilename();
        String fullPath = rootDir + "/" + dir + uuid;
        if (filename != null && filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf("."));
            fullPath += extension;
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            client.putObject(new PutObjectRequest(bucketName, fullPath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
//            throw new FileUploadFailedException();
            throw new RuntimeException();
        }

        return client.getUrl(bucketName, fullPath).toString();
    }
}
