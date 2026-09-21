package com.gustavo.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("API REST para gerenciamento de tarefas e lançamentos financeiros")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gustavo Ben Abraham")
                                .email("gustavo@example.com")
                                .url("https://github.com/GustavoBenAbraham"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento"),
                        new Server().url("https://api.taskmanager.com").description("Servidor de Produção")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtido no endpoint /api/v1/auth/login")));
    }
}
