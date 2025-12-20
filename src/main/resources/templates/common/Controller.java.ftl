package ${packageName}.controller;

import ${packageName}.entity.${className};
import ${packageName}.service.I${className}Service;
import ${packageName}.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
<#if techStack.useSwagger>
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
</#if>

import java.util.List;

<#assign displayName = (tableComment?has_content)?then(tableComment, className)>
/**
 * ${displayName} 控制层
 */
<#if techStack.useSwagger>
@Tag(name = "${displayName}", description = "${displayName}管理接口")
</#if>
@RestController
@RequestMapping("/${className?lower_case}")
public class ${className}Controller {

    @Autowired
    private I${className}Service ${className?uncap_first}Service;

    <#if techStack.useSwagger>
    @Operation(summary = "添加${displayName}", description = "创建新的${displayName}记录")
    </#if>
    @PostMapping("/add")
    public Result${'<'}${'String'}${'>'} add(<#if techStack.useSwagger>@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "${displayName}实体对象", required = true) </#if>@RequestBody ${className} record) {
        int count = ${className?uncap_first}Service.insert(record);
        if (count > 0) {
            return Result.success("添加成功");
        }
        return Result.failed("添加失败");
    }

    <#if techStack.useSwagger>
    @Operation(summary = "删除${displayName}", description = "根据ID删除${displayName}记录")
    </#if>
    @DeleteMapping("/delete/{id}")
    public Result${'<'}${'String'}${'>'} delete(<#if techStack.useSwagger>@Parameter(description = "${displayName}ID", required = true) </#if>@PathVariable ${primaryKeyType} id) {
        int count = ${className?uncap_first}Service.deleteById(id);
        if (count > 0) {
            return Result.success("删除成功");
        }
        return Result.failed("删除失败");
    }

    <#if techStack.useSwagger>
    @Operation(summary = "更新${displayName}", description = "更新${displayName}记录")
    </#if>
    @PostMapping("/update")
    public Result${'<'}${'String'}${'>'} update(<#if techStack.useSwagger>@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "${displayName}实体对象", required = true) </#if>@RequestBody ${className} record) {
        int count = ${className?uncap_first}Service.updateById(record);
        if (count > 0) {
            return Result.success("更新成功");
        }
        return Result.failed("更新失败");
    }

    <#if techStack.useSwagger>
    @Operation(summary = "查询${displayName}", description = "根据ID查询${displayName}详情")
    </#if>
    @GetMapping("/get/{id}")
    public Result${'<'}${className}${'>'} get(<#if techStack.useSwagger>@Parameter(description = "${displayName}ID", required = true) </#if>@PathVariable ${primaryKeyType} id) {
        ${className} data = ${className?uncap_first}Service.selectById(id);
        return Result.success(data);
    }

    <#if techStack.useSwagger>
    @Operation(summary = "查询${displayName}列表", description = "获取所有${displayName}记录")
    </#if>
    @GetMapping("/list")
    public Result${'<'}List${'<'}${className}${'>'}${'>'} list() {
        List${'<'}${className}${'>'} list = ${className?uncap_first}Service.selectAll();
        return Result.success(list);
    }
}

