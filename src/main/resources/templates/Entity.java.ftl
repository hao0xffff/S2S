package ${packageName}.entity;

import lombok.Data;
<#list imports as importPath>
    import ${importPath};
</#list>

/**
* ${tableComment}
*/
@Data
public class ${className} {
<#list columns as col>
    /**
    * ${col.comment}
    */
    private ${col.javaType} ${col.propertyName};

</#list>
}