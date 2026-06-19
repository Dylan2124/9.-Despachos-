package cl.dsy1103despacho.service.client;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI despachoOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Despachos")
                        .version("1.0.0")
                        .description("Microservicio encargado de gestionar las guías de despacho y estados de envío.")
                        .contact(new Contact()
                                .name("Matias Tiznado")
                                .email("m.tiznado@duocuc.cl")))
                .servers(List.of(
                        // Entrada por el Gateway
                        new Server().url("http://localhost:8080").description("API Gateway"),
                        // Entrada directa al microservicio
                        new Server().url("http://localhost:8089").description("Microservicio Despachos - Local")
                ));
    }
}