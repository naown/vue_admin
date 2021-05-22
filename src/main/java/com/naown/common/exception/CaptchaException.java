package com.naown.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: chenjian
 * @since: 2021/5/13 22:43 周四
 **/
public class CaptchaException extends AuthenticationException {
    public CaptchaException(String msg) {
        super(msg);
    }
}
