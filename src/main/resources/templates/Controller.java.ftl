package ${packageName}.controller;

import ${packageName}.entity.${className};
import ${packageName}.service.I${className}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* ${tableComment} 控制层
*/
@RestController
@RequestMapping("/${className?lower_case}")
public class ${className}Controller {

@Autowired
private I${className}Service ${className?uncap_first}Service;

@PostMapping("/add")
public String add(@RequestBody ${className} record) {
return ${className?uncap_first}Service.insert(record) > 0 ? "添加成功" : "添加失败";
}

@DeleteMapping("/delete/{id}")
public String delete(@PathVariable Long id) {
return ${className?uncap_first}Service.deleteById(id) > 0 ? "删除成功" : "删除失败";
}

@PostMapping("/update")
public String update(@RequestBody ${className} record) {
return ${className?uncap_first}Service.updateById(record) > 0 ? "更新成功" : "更新失败";
}

@GetMapping("/get/{id}")
public ${className} get(@PathVariable Long id) {
return ${className?uncap_first}Service.selectById(id);
}

@GetMapping("/list")
public List<${className}> list() {
return ${className?uncap_first}Service.selectAll();
}
}