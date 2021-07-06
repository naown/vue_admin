package com.naown.common.generate.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Lists;
import com.naown.common.generate.entity.ColumnEntity;
import com.naown.common.generate.entity.TableEntity;
import com.naown.common.generate.entity.TableRequest;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 * @author yangkai.shen
 * @date Created in 2019-03-22 09:27
 */
@Slf4j
@UtilityClass
public class CodeGenUtil {

    private final String ENTITY_JAVA_VM = "Entity.java.vm";
    private final String MAPPER_JAVA_VM = "Mapper.java.vm";
    private final String SERVICE_JAVA_VM = "Service.java.vm";
    private final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    private final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    private final String MAPPER_XML_VM = "Mapper.xml.vm";
    private final String API_JS_VM = "api.js.vm";

    private List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("config/Entity.java.vm");
        templates.add("config/Mapper.java.vm");
        templates.add("config/Mapper.xml.vm");
        templates.add("config/Service.java.vm");
        templates.add("config/ServiceImpl.java.vm");
        templates.add("config/Controller.java.vm");
        templates.add("config/api.js.vm");
        return templates;
    }

    /**
     * 生成代码
     * @param request HttpRequest
     * @param table 表信息
     * @param columns 列信息
     * @param zip 压缩包资源类
     */
    @SneakyThrows
    public void generatorCode(TableRequest request, Entity table, List<Entity> columns, ZipOutputStream zip) {
        //配置信息

        Properties propsDB2Java = getPropertiesConfig("config/generator.properties");
        Properties propsDB2Jdbc = getPropertiesConfig("config/jdbc_type.properties");

        //Props propsDB2Java = getConfig("config/generator.properties");
        //Props propsDB2Jdbc = getConfig("config/jdbc_type.properties");

        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.getStr("tableName"));

        // TODO 注释
        tableEntity.setComments(table.getStr("tableComment"));
        // TODO 前缀
        String tablePrefix = propsDB2Java.getProperty("tablePrefix");

        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(className);
        tableEntity.setLowerClassName(StrUtil.lowerFirst(className));

        //列信息
        List<ColumnEntity> columnList = Lists.newArrayList();
        for (Entity column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.getStr("columnName"));
            columnEntity.setDataType(column.getStr("dataType"));
            columnEntity.setComments(column.getStr("columnComment"));
            columnEntity.setExtra(column.getStr("extra"));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StrUtil.lowerFirst(attrName));

            //列的数据类型，转换成Java类型
            String attrType = propsDB2Java.getProperty(columnEntity.getDataType(), "unknownType");
            columnEntity.setAttrType(attrType);
            String jdbcType = propsDB2Jdbc.getProperty(columnEntity.getDataType(), "unknownType");
            columnEntity.setJdbcType(jdbcType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.getStr("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        //封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getCaseClassName());
        map.put("classname", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("datetime", DateUtil.now());
        map.put("year", DateUtil.year(new Date()));
        // TODO 注释
        map.put("comments", tableEntity.getComments());
        // TODO 作者
        map.put("author", propsDB2Java.getProperty("author"));
        // TODO 模块名称
        map.put("moduleName", propsDB2Java.getProperty("moduleName"));
        // TODO 包名
        map.put("package", propsDB2Java.getProperty("package"));
        map.put("mainPath", propsDB2Java.getProperty("mainPath"));

        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template);
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(Objects.requireNonNull(getFileName(template, tableEntity.getCaseClassName(), map.get("package").toString()))));
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                IoUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     * @param columnName 列名
     * @return
     */
    private String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, '_').replace("_", "");
    }

    /**
     * 表名转换成Java类名
     * @param tableName 表名
     * @param tablePrefix 表前缀
     * @return
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StrUtil.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     * @param fileName 配置文件名称路径
     * @return
     */
    @SneakyThrows
    private Props getConfig(String fileName) {
        Props props = new Props(fileName);
        props.autoLoad(true);
        return props;
    }

    @SneakyThrows
    private Properties getPropertiesConfig(String fileNamePath){
        ClassPathResource resource = new ClassPathResource(fileNamePath);
        InputStream stream = resource.getInputStream();
        Properties props = new Properties();
        props.load(stream);
        return props;
    }

    /**
     * 获取文件名
     * @param template 表参数
     * @param className 类名称
     * @param packageName 包名称
     * @return
     */
    private String getFileName(String template, String className, String packageName) {
        // 包路径
        String packagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        // 资源路径
        String resourcePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        // api路径
        String apiPath = "api" + File.separator;

        if (StrUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }
        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + className + ".java";
        }
        if (template.contains(MAPPER_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }
        if (template.contains(SERVICE_JAVA_VM)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }
        if (template.contains(SERVICE_IMPL_JAVA_VM)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }
        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }
        if (template.contains(MAPPER_XML_VM)) {
            return resourcePath + "mapper" + File.separator + className + "Mapper.xml";
        }
        if (template.contains(API_JS_VM)) {
            return apiPath + className.toLowerCase() + ".js";
        }
        return null;
    }
}
