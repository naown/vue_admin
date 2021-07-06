package com.naown.common.generate.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author: chenjian
 * @since: 2021/6/28 15:08 周一
 **/
@Data
public class DbEntity {
    /** 表名 */
    private String tableName;
    /** 存储引擎 */
    private String engine;
    /** 表注释 */
    private String tableComment;
    /** 字符编码集 */
    private String tableCollation;
    /** 表的创建时间 */
    private Timestamp createTime;
}
