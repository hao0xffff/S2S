package ${packageName};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* ${projectName!} 启动类
* 由 S2S 引擎自动生成
*/
@SpringBootApplication
@MapperScan("${packageName}.mapper")
public class ${className}Application {

public static void main(String[] args) {
SpringApplication.run(${className}Application.class, args);
}

}