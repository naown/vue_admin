package com.naown.common.generate.service;

import cn.hutool.db.Entity;
import com.naown.common.generate.entity.PageResult;
import com.naown.common.generate.entity.TableRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 代码生成器
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:15
 */
public interface CodeGenService {
    /**
     * 生成代码
     * @param request 生成配置
     * @return 代码压缩文件
     */
    byte[] generatorCode(TableRequest request);

    /**
     * 分页查询表信息
     * @param request 请求参数
     * @return 表名分页信息
     */
    PageResult<Entity> listTables(TableRequest request, HttpServletRequest httpServletRequest);
}
