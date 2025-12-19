package ${packageName}.service;

import ${packageName}.entity.${className};
import java.util.List;

/**
* ${tableComment} Service 接口
*/
public interface I${className}Service {

int insert(${className} record);

int deleteById(Long id);

int updateById(${className} record);

${className} selectById(Long id);

List<${className}> selectAll();
}