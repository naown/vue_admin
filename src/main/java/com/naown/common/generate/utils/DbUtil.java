package com.naown.common.generate.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.naown.common.generate.entity.TableRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据库工具类
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:26
 */
@Slf4j
@UtilityClass
public class DbUtil {
    public DruidDataSource buildFromTableRequest(TableRequest request) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(request.getPrepend() + request.getUrl());
        dataSource.setUsername(request.getUsername());
        dataSource.setPassword(request.getPassword());
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}
