package com.naown.common.generate.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import com.alibaba.druid.pool.DruidDataSource;
import com.naown.common.generate.entity.PageResult;
import com.naown.common.generate.entity.TableRequest;
import com.naown.common.generate.service.CodeGenService;
import com.naown.common.generate.utils.CodeGenUtil;
import com.naown.common.generate.utils.DbUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:15
 */
@Service
@AllArgsConstructor
public class CodeGenServiceImpl implements CodeGenService {

    /** 查询表的表名 表使用的索引比如InnoDB 表的注释 表的创建时间 */
    private final String TABLE_SQL_TEMPLATE = "select table_name as tableName, engine as engine, table_comment as tableComment, create_time as createTime ,table_collation as tableCollation from information_schema.tables where table_schema = (select database()) %s order by create_time desc";
    /** 查询表的字段名 字段类型 字段注释 字段主键 额外的配置(比如说主键的自增等等) */
    private final String COLUMN_SQL_TEMPLATE = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns where table_name = ? and table_schema = (select database()) order by ordinal_position";
    /** 查询该数据库表的个数 */
    private final String COUNT_SQL_TEMPLATE = "select count(1) from (%s)tmp";
    /** 是否需要分页查询 使用limit限制 */
    private final String PAGE_SQL_TEMPLATE = " limit ?";

    /**
     * 分页查询表信息
     *
     * @param request 请求参数
     * @return 表名分页信息
     */
    @Override
    @SneakyThrows
    public PageResult<Entity> listTables(TableRequest request, HttpServletRequest httpServletRequest) {
        DruidDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        int current = request.getCurrentPage();
        int size = request.getPageSize();

        String paramSql = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(request.getTableName())) {
            paramSql = "and table_name like concat('%', ?, '%')";
        }
        String sql = String.format(TABLE_SQL_TEMPLATE, paramSql);
        String countSql = String.format(COUNT_SQL_TEMPLATE, sql);

        List<Entity> query;
        BigDecimal count;
        if (StrUtil.isNotBlank(request.getTableName())) {
            List<Entity> query1 = db.query(sql + PAGE_SQL_TEMPLATE,new EntityListHandler(false), request.getTableName(), current, size);
            query = db.query(sql + PAGE_SQL_TEMPLATE, request.getTableName(), current, size);
            count = (BigDecimal) db.queryNumber(countSql, request.getTableName());
        } else {
            query = db.query(sql + String.format(" limit %s",size * (current-1) +","+size),new EntityListHandler(false));
            count = (BigDecimal) db.queryNumber(countSql);
        }

        PageResult<Entity> pageResult = new PageResult<>(count.longValue(), current, size, query);

        dataSource.close();
        return pageResult;
    }

    /**
     * 生成代码
     *
     * @param request 生成配置
     * @return 代码压缩文件
     */
    @Override
    public byte[] generatorCode(TableRequest request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        //查询表信息
        Entity table = queryTable(request);
        //查询列信息
        List<Entity> columns = queryColumns(request);
        //生成代码
        CodeGenUtil.generatorCode(request, table, columns, zip);
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    @SneakyThrows
    private Entity queryTable(TableRequest request) {
        DruidDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        String paramSql = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(request.getTableName())) {
            paramSql = "and table_name = ?";
        }
        String sql = String.format(TABLE_SQL_TEMPLATE, paramSql);
        Entity entity = db.queryOne(sql, request.getTableName());

        dataSource.close();
        return entity;
    }

    @SneakyThrows
    private List<Entity> queryColumns(TableRequest request) {
        DruidDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        List<Entity> query = db.query(COLUMN_SQL_TEMPLATE, request.getTableName());

        dataSource.close();
        return query;
    }

}
