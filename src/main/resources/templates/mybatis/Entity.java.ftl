package ${packageName}.entity;

import lombok.Data;
<#list imports as importPath>
import ${importPath};
</#list>

/**
 * ${tableComment}
 */
<#if techStack.useSwagger>
@Schema(description = "${tableComment}")
</#if>
@Data
public class ${className} {
<#list columns as col>
    /**
     * ${col.comment}
     <#if col.rawDbType??>     * DB Type: ${col.rawDbType}</#if>
     <#if col.defaultValue??>   * Default: ${col.defaultValue}</#if>
     */
    <#if techStack.useSwagger>
    @Schema(description = "${col.comment!col.propertyName}"<#if col.defaultValue??>, example = "${col.defaultValue}"</#if>)
    </#if>
    <#if techStack.useValidation && !col.primaryKey>
        <#if !col.nullable>
    @NotNull(message = "${col.comment!col.propertyName}不能为空")
        </#if>
        <#if col.maxLength?? && col.javaType == "String">
    @Size(max = ${col.maxLength}, message = "${col.comment!col.propertyName}长度不能超过${col.maxLength}个字符")
        </#if>
    </#if>
    private ${col.javaType} ${col.propertyName};

</#list>
}

