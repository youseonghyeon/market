package com.project.market.infra.fileupload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Profile("test")
public class AwsS3ServiceTest implements AwsS3Service {

    public String uploadFile(String dir, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.info("파일이 존재하지 않습니다.");
            return "파일이 존재하지 않습니다.";
        }
        return "저장 성공";

    }
}
