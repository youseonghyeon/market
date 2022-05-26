package com.project.market.infra.fileupload;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {

    String uploadFile(String dir, MultipartFile multipartFile);
}
