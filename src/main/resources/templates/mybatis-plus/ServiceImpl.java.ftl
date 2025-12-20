package ${packageName}.service.impl;

import ${packageName}.entity.${className};
import ${packageName}.mapper.${className}Mapper;
import ${packageName}.service.I${className}Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * ${tableComment} Service 实现类
 * 使用 MyBatis-Plus BaseMapper 提供的方法
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
    public int deleteById(${primaryKeyType} id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int updateById(${className} record) {
        return baseMapper.updateById(record);
    }

    @Override
    public ${className} selectById(${primaryKeyType} id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<${className}> selectAll() {
        // 使用 MyBatis-Plus 的 selectList，传入 null 表示查询所有
        return baseMapper.selectList(null);
    }
}

