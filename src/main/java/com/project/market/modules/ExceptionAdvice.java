package com.project.market.modules;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MissingPathVariableException.class)
    public String missingPathExceptionHandler(Model model) {
        model.addAttribute("error", "페이지에 접근할 수 없습니다.");
        return "error/missing-path-variable-exception";
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public String unAuthorizedExceptionHandler(Model model, Exception e) {
        String message = e.getMessage();
        model.addAttribute(message);
        return "error/missing-path-variable-exception";
    }


//    @ExceptionHandler
//    public String exceptionHandler(@CurrentAccount Account account, HttpServletRequest request, RuntimeException e) {
//        if (account != null) {
//            log.info("'{}', requested '{}'", account.getNickname(), request.getRequestURI());
//        } else  {
//            log.info("requested '{}'", request.getRequestURI());
//        }
//        log.error("bad request", e);
//        return "error";
//    }
}
