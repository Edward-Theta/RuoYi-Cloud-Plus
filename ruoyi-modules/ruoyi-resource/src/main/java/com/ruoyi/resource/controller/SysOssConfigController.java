package com.ruoyi.resource.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.resource.domain.bo.SysOssConfigBo;
import com.ruoyi.resource.domain.vo.SysOssConfigVo;
import com.ruoyi.resource.service.ISysOssConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 对象存储配置Controller
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Validated
@Api(value = "对象存储配置控制器", tags = {"对象存储配置管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/oss/config")
public class SysOssConfigController extends BaseController {

    private final ISysOssConfigService iSysOssConfigService;

    /**
     * 查询对象存储配置列表
     */
    @ApiOperation("查询对象存储配置列表")
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssConfigVo> list(@Validated(QueryGroup.class) SysOssConfigBo bo, PageQuery pageQuery) {
        return iSysOssConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取对象存储配置详细信息
     */
    @ApiOperation("获取对象存储配置详细信息")
    @SaCheckPermission("system:oss:query")
    @GetMapping("/{ossConfigId}")
    public R<SysOssConfigVo> getInfo(@ApiParam("OSS配置ID")
                                              @NotNull(message = "主键不能为空")
                                              @PathVariable("ossConfigId") Integer ossConfigId) {
        return R.ok(iSysOssConfigService.queryById(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @ApiOperation("新增对象存储配置")
    @SaCheckPermission("system:oss:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(iSysOssConfigService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改对象存储配置
     */
    @ApiOperation("修改对象存储配置")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(iSysOssConfigService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除对象存储配置
     */
    @ApiOperation("删除对象存储配置")
    @SaCheckPermission("system:oss:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    public R<Void> remove(@ApiParam("OSS配置ID串")
                                   @NotEmpty(message = "主键不能为空")
                                   @PathVariable Long[] ossConfigIds) {
        return toAjax(iSysOssConfigService.deleteWithValidByIds(Arrays.asList(ossConfigIds), true) ? 1 : 0);
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysOssConfigBo bo) {
        return toAjax(iSysOssConfigService.updateOssConfigStatus(bo));
    }
}
