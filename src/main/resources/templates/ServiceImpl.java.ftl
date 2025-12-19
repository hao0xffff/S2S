package ${packageName}.service.impl;

import ${packageName}.entity.${className};
import ${packageName}.mapper.${className}Mapper;
import ${packageName}.service.I${className}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
* ${tableComment} Service 实现类
*/
@Service
public class ${className}ServiceImpl implements I${className}Service {

@Autowired
private ${className}Mapper baseMapper;

@Override
public int insert(${className} record) {
return baseMapper.insert(record);
}

@Override
public int deleteById(Long id) {
return baseMapper.deleteById(id);
}

@Override
public int updateById(${className} record) {
return baseMapper.updateById(record);
}

@Override
public ${className} selectById(Long id) {
return baseMapper.selectById(id);
}

@Override
public List<${className}> selectAll() {
return baseMapper.selectAll();
}
}