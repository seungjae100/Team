package com.web.team.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("그룹웨어 API 문서")
                .description("Spring Boot 3 기반 그룹웨어 프로젝트의 API 문서입니다.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("백엔드 개발자")
                        .email("user@gmail.com")
                        .url("https://github.com/team"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/license/"));


    }
}
