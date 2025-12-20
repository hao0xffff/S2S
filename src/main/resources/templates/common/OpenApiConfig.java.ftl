package ${packageName}.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 3 Configuration
 * API Documentation: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("${projectName} API 文档")
                        .description("基于 S2S V3.0 Strategy Architecture 构建的 RESTful API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("S2S Generator")
                                .email("support@example.com")));
    }
}

