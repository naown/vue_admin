package com.naown.common.generate.controller;

import cn.hutool.core.io.IoUtil;
import com.naown.common.generate.entity.TableRequest;
import com.naown.common.generate.service.CodeGenService;
import com.naown.common.lang.Result;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 代码生成器
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:11
 */
@RestController
@AllArgsConstructor
@RequestMapping("/generator")
public class CodeGenerateController {
    private final CodeGenService codeGenService;

    /**
     * 列表
     *
     * @param request 参数集
     * @return 数据库表
     */
    @GetMapping("/table")
    public Result listTables(TableRequest request, HttpServletRequest httpServletRequest) {
        return Result.succeed(codeGenService.listTables(request,httpServletRequest));
    }

    /**
     * 生成代码
     * @param tableRequest table参数
     * @param response HttpResponse
     * @param request HttpRequest
     */
    @SneakyThrows
    @PostMapping("/code")
    public void generatorCode(@RequestBody TableRequest tableRequest, HttpServletResponse response,HttpServletRequest request) {
        Assert.isTrue(tableRequest.getUrl() != null,"Url不能为空");
        byte[] data = codeGenService.generatorCode(tableRequest);

        response.reset();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.zip", tableRequest.getTableName()));
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
    }
}
