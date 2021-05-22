package com.naown.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenjian
 * @since: 2021/5/22 21:52 周六
 **/
@Data
public class PasswordDTO implements Serializable {
    /** 旧密码 */
    @NotNull(message = "旧密码不能为空")
    private String oldPassword;
    /** 新密码 */
    @NotNull(message = "新密码不能为空")
    private String newPassword;
}
