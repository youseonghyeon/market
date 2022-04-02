package com.project.market.modules.account.controller;

import com.project.market.infra.mail.MailSender;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HelpController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final MailSender mailSender;

    @GetMapping("/help/find-id")
    public String findLonginIdForm() {
        return "account/help/find-id";
    }

    @PostMapping("/help/find-id")
    public String findLoginId(@RequestParam("email") String email, RedirectAttributes attributes, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            attributes.addFlashAttribute("message", "등록된 회원이 존재하지 않습니다.");
            return "redirect:/help/find-id";
        }
        model.addAttribute("id", account.getLoginId());
        return "account/help/login-id";
    }


    @GetMapping("/help/find-password")
    public String findPasswordForm() {
        return "account/help/find-password";
    }

    @PostMapping("/help/find-password") // 비밀번호 찾기 -> 이메일 인증 -> 토큰 메일 전송
    public String sendMail(@RequestParam("loginId") String loginId, HttpServletResponse response, Model model) {
        Account account = accountRepository.findByLoginId(loginId);
        if (account == null) {
            model.addAttribute("message", "error");
            return "account/help/find-password";
        }
        String token = accountService.createPasswordToken(account);
        mailSender.send(account.getEmail(), token);

        createCookie("temp_loginId", loginId, response);
        // TODO(DANGER)  post("/help/modify/password")에 바로 접근할 경우 인증을 거치지 않은채 바로 비밀번호가 변경됨.
        // -> 검증 로직 생성
        return "redirect:/help/send-token";
    }

    private void createCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(600);
        response.addCookie(cookie);
    }

    @GetMapping("/help/send-token")
    public String completeForm() {
        return "account/help/success";
    }

    @GetMapping("/help/confirm")
    public String tokenCertification(@RequestParam("token") String token) {
        Account account = accountRepository.findByPasswordToken(token);
        if (account == null || !account.isValidPasswordToken(token)) {
            return "account/help/fail";
        }
        return "account/help/modify-password";
    }

    @PostMapping("/help/modify/password")
    public String modifyPassword(@CookieValue(value = "temp_loginId") Cookie idCookie,
                                 @RequestParam("new-password") String password, HttpServletResponse response) {
        Account account = accountRepository.findByLoginId(idCookie.getValue());
        if (account == null || !account.isValidPasswordToken(account.getPasswordToken())) {
            return "account/help/fail";
        }

        accountService.modifyPassword(account, password);
        accountService.destroyPasswordToken(account);
        destroyCookie(idCookie, response);
        return "redirect:/login";
    }

    private void destroyCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setValue(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
