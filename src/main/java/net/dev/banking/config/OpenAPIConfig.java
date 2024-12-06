package net.dev.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        Resource resource = new ClassPathResource("openapi/banking-api.yaml");
        String openApiContent = new String(Files.readAllBytes(resource.getFile().toPath()));
        return new OpenAPIV3Parser().readContents(openApiContent).getOpenAPI();
    }
}