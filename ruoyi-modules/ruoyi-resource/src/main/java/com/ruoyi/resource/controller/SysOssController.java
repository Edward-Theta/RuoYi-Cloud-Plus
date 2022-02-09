package com.ruoyi.resource.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.file.FileUtils;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.resource.domain.SysOss;
import com.ruoyi.resource.domain.bo.SysOssBo;
import com.ruoyi.resource.domain.vo.SysOssVo;
import com.ruoyi.resource.service.ISysOssService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传 控制层
 *
 * @author Lion Li
 */
@Validated
@Api(value = "对象存储控制器", tags = {"对象存储管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/oss")
public class SysOssController extends BaseController {

    private final ISysOssService iSysOssService;

    /**
     * 查询OSS对象存储列表
     */
    @ApiOperation("查询OSS对象存储列表")
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssVo> list(@Validated(QueryGroup.class) SysOssBo bo, PageQuery pageQuery) {
        return iSysOssService.queryPageList(bo, pageQuery);
    }

    /**
     * 上传OSS对象存储
     */
    @ApiOperation("上传OSS对象存储")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = File.class, required = true),
    })
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            throw new ServiceException("上传文件不能为空");
        }
        SysOss oss = iSysOssService.upload(file);
        Map<String, String> map = new HashMap<>(2);
        map.put("url", oss.getUrl());
        map.put("fileName", oss.getFileName());
        return R.ok(map);
    }

    @ApiOperation("下载OSS对象存储")
    @SaCheckPermission("system:oss:download")
    @GetMapping("/download/{ossId}")
    public void download(@ApiParam("OSS对象ID") @PathVariable Long ossId, HttpServletResponse response) throws IOException {
        SysOss sysOss = iSysOssService.getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        response.reset();
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        long data;
        try {
            data = HttpUtil.download(sysOss.getUrl(), response.getOutputStream(), false);
        } catch (HttpException e) {
            if (e.getMessage().contains("403")) {
                throw new ServiceException("无读取权限, 请在对应的OSS开启'公有读'权限!");
            } else {
                throw new ServiceException(e.getMessage());
            }
        }
        response.setContentLength(Convert.toInt(data));
    }

    /**
     * 删除OSS对象存储
     */
    @ApiOperation("删除OSS对象存储")
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    public R<Void> remove(@ApiParam("OSS对象ID串")
                                   @NotEmpty(message = "主键不能为空")
                                   @PathVariable Long[] ossIds) {
        return toAjax(iSysOssService.deleteWithValidByIds(Arrays.asList(ossIds), true) ? 1 : 0);
    }

}
