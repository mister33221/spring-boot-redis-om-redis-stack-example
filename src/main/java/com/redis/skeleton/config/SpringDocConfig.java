package com.redis.skeleton.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "This is a Redis OM Skeleton practice project",
                version = "1.0",
                description =
                                """
                                版本
                                
                                Java : 17
                                
                                spring boot : 3.2.1
                                
                                Radis OM : 0.8.8
                                
                                springdoc-openapi-core : 2.0.2
                                
                                """)
)
@Configuration
public class SpringDocConfig {
}
