package com.naown.common.handler;

import com.naown.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * @author: chenjian
 * @since: 2021/5/9 23:10 周日
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result handle(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();
        log.error("实体校验异常:{}",objectError.getDefaultMessage());
        return Result.error(objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handle(IllegalArgumentException e){
        log.error("Assert异常:{}",e.getMessage());
        return Result.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handle(RuntimeException e){
        log.error("运行时异常:{}",e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 权限异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result handle(AccessDeniedException e){
        log.error("权限异常:{}",e.getMessage());
        return Result.error(e.getMessage());
    }
}
