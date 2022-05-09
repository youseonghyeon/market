package com.project.market.modules;

import com.project.market.infra.exception.CustomNotFoundException;
import com.project.market.infra.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        log.info("message={}", message);
        model.addAttribute("msg", message);
        log.info("UnAuthorizedException");
        return "exception/unauthorize";
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public String customNotFoundExceptionHandler(CustomNotFoundException e) {
        String message = e.getMessage();
        log.info("message={}", message, e);
        return "404";
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
