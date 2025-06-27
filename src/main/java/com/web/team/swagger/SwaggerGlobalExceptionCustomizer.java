package com.web.team.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class SwaggerGlobalExceptionCustomizer {

    @Bean
    public OpenApiCustomizer globalResponseCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();

            // 공통 에러 스키마
            Schema<?> errorSchema = new ObjectSchema()
                    .addProperty("message", new StringSchema().example("잘못된 요청입니다."))
                    .addProperty("status", new IntegerSchema().example(400))
                    .addProperty("detail", new StringSchema().example("입력 값이 유효하지 않습니다."));

            // 공통 응답 정의
            components.addResponses("BadRequest",
                    new ApiResponse()
                            .description("잘못된 요청")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema))));

            components.addResponses("Unauthorized",
                    new ApiResponse()
                            .description("인증 실패")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema))));

            components.addResponses("Forbidden",
                    new ApiResponse()
                            .description("권한 없음")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema))));

            components.addResponses("NotFound",
                    new ApiResponse()
                            .description("리소스를 찾을 수 없음")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema))));

            components.addResponses("InternalServerError",
                    new ApiResponse()
                            .description("서버 오류")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema))));
        };
    }
}
