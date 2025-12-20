package ${packageName}.mapper;

import ${packageName}.entity.${className};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ${tableComment} Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，提供基础CRUD方法
 */
@Mapper
public interface ${className}Mapper extends BaseMapper<${className}> {
    // MyBatis-Plus BaseMapper 已提供以下方法：
    // - insert(entity) - 插入一条记录
    // - deleteById(id) - 根据ID删除
    // - updateById(entity) - 根据ID更新
    // - selectById(id) - 根据ID查询
    // - selectList(wrapper) - 查询列表
    // - selectPage(page, wrapper) - 分页查询
    // 如需自定义方法，可在此添加
}

