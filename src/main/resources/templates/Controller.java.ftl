package ${packageName}.controller;

import ${packageName}.entity.${className};
import ${packageName}.service.I${className}Service;
import ${packageName}.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* ${tableComment!} 控制层
*/
@RestController
@RequestMapping("/${className?lower_case}")
public class ${className}Controller {

@Autowired
private I${className}Service ${className?uncap_first}Service;

@PostMapping("/add")
public Result${'<'}${'String'}${'>'} add(@RequestBody ${className} record) {
int count = ${className?uncap_first}Service.insert(record);
if (count > 0) {
return Result.success("添加成功");
}
return Result.failed("添加失败");
}

@DeleteMapping("/delete/{id}")
public Result${'<'}${'String'}${'>'} delete(@PathVariable Long id) {
int count = ${className?uncap_first}Service.deleteById(id);
if (count > 0) {
return Result.success("删除成功");
}
return Result.failed("删除失败");
}

@PostMapping("/update")
public Result${'<'}${'String'}${'>'} update(@RequestBody ${className} record) {
int count = ${className?uncap_first}Service.updateById(record);
if (count > 0) {
return Result.success("更新成功");
}
return Result.failed("更新失败");
}

@GetMapping("/get/{id}")
public Result${'<'}${className}${'>'} get(@PathVariable Long id) {
${className} data = ${className?uncap_first}Service.selectById(id);
return Result.success(data);
}

@GetMapping("/list")
public Result${'<'}List${'<'}${className}${'>'}${'>'} list() {
List${'<'}${className}${'>'} list = ${className?uncap_first}Service.selectAll();
return Result.success(list);
}
}