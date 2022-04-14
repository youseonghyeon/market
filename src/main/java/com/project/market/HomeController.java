package com.project.market;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/test")
    public String testPage() {
        return "test";
    }

    @PostMapping("/test")
    public String testFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        log.info("name={}", name);
        log.info("콘텐트 타입{}", file.getContentType());
        log.info("파일 이름{}", file.getOriginalFilename());
        log.info("이름{}", file.getName());
        log.info("크기{}", file.getSize());
        return "test";
    }
}
