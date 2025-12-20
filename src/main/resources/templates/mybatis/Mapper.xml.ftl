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
        DELETE FROM ${tableName} WHERE ${primaryKeyColumnName!columns[0].columnName} = ${r'#{id}'}
    </delete>

    <update id="updateById" parameterType="${packageName}.entity.${className}">
        UPDATE ${tableName}
        <set>
            <#list columns as col>
                <#if !col.primaryKey> <#-- 排除主键列 -->
                    <if test="${col.propertyName} != null">
                        ${col.columnName} = ${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if>
                    </if>
                </#if>
            </#list>
        </set>
        WHERE ${primaryKeyColumnName!columns[0].columnName} = ${r'#{'}${primaryKeyPropertyName!columns[0].propertyName}${r'}'}
    </update>

    <select id="selectById" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
        WHERE ${primaryKeyColumnName!columns[0].columnName} = ${r'#{id}'}
    </select>

    <select id="selectAll" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
    </select>

</mapper>

