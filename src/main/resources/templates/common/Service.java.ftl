package ${packageName}.service;

import ${packageName}.entity.${className};
import java.util.List;

/**
 * ${tableComment} Service 接口
 */
public interface I${className}Service {

    int insert(${className} record);

    int deleteById(${primaryKeyType} id);

    int updateById(${className} record);

    ${className} selectById(${primaryKeyType} id);

    List<${className}> selectAll();
}

