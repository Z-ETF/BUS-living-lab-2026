package com.iot.soil.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server prodServer = new Server();
        prodServer.setUrl("https://3.65.31.55:8443");
        prodServer.setDescription("Production Server");

        Server devServer = new Server();
        devServer.setUrl("https://localhost:8443");
        devServer.setDescription("Development Server");

        // ...existing code...
        Contact contact = new Contact();
        contact.setEmail("support@example.com");
        contact.setName("Soil Sensor API");
        contact.setUrl("https://example.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Soil Sensor Data API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for managing soil sensor data.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(prodServer, devServer));
    }
}
