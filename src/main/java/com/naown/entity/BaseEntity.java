package com.naown.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: chenjian
 * @since: 2021/5/9 22:58 周日
 **/
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 创建时间 */
    private LocalDateTime created;

    /** 更新时间 */
    private LocalDateTime updated;

    /** 状态 */
    private Integer status;
}
