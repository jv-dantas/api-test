package com.exemplo.provaapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Produtos")
                        .version("1.0")
                        .description("Documentação da API REST para gerenciamento de produtos."));
        // Para testar o layout da documentação springDoc no navegador
        // http://localhost:8080/swagger-ui/index.html#/
    }
}
