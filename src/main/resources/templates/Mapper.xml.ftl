<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${className}Mapper">

    <resultMap id="BaseResultMap" type="${packageName}.entity.${className}">
        <#list columns as col>
            <result column="${col.columnName}" property="${col.propertyName}" />
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list columns as col>${col.columnName}<#if col_has_next>, </#if></#list>
    </sql>

    <insert id="insert" parameterType="${packageName}.entity.${className}">
        INSERT INTO ${tableName} (
        <#list columns as col>${col.columnName}<#if col_has_next>, </#if></#list>
        ) VALUES (
        <#list columns as col>${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>, </#if></#list>
        )
    </insert>

    <delete id="deleteById">
        DELETE FROM ${tableName} WHERE ${columns[0].columnName} = ${r'#{'}${columns[0].propertyName}${r'}'}
    </delete>

    <update id="updateById" parameterType="${packageName}.entity.${className}">
        UPDATE ${tableName}
        <set>
            <#list columns as col>
                <#if col_index != 0> <#-- 排除第一列主键 -->
                    <if test="${col.propertyName} != null">
                        ${col.columnName} = ${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if>
                    </if>
                </#if>
            </#list>
        </set>
        WHERE ${columns[0].columnName} = ${r'#{'}${columns[0].propertyName}${r'}'}
    </update>

    <select id="selectById" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
        WHERE ${columns[0].columnName} = ${r'#{'}${columns[0].propertyName}${r'}'}
    </select>

    <select id="selectAll" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
    </select>

</mapper>