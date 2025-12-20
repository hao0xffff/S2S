package ${packageName}.mapper;

import ${packageName}.entity.${className};
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * ${tableComment} Mapper 接口
 */
@Mapper
public interface ${className}Mapper {

    /**
     * 新增记录
     */
    int insert(${className} record);

    /**
     * 根据主键删除
     */
    int deleteById(${primaryKeyType} id);

    /**
     * 根据主键动态更新
     */
    int updateById(${className} record);

    /**
     * 根据主键查询
     */
    ${className} selectById(${primaryKeyType} id);

    /**
     * 查询所有记录
     */
    List<${className}> selectAll();
}

