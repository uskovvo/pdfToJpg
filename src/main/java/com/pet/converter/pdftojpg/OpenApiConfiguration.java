package com.pet.converter.pdftojpg;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApiDescription() {
        Server localhost = new Server();
        localhost.setUrl("http://localhost:8080");
        localhost.setDescription("Local env");

        Server productionServer = new Server();
        productionServer.setUrl("http://localhost:8081");
        productionServer.setDescription("Production env");

        Contact contact = new Contact();
        contact.setName("Uskov Valerii");
        contact.setEmail("uskovvobdt@gmail.com");
        contact.setUrl("http://localhost:8080");

        License license = new License()
                .name("GNU AGPLv3")
                .url("https://choosealicense.com/licenses/agpl-3.0/");

        Info info = new Info()
                .title("Client orders API")
                .version("1.0")
                .contact(contact)
                .description("API for client orders")
                .termsOfService("https://choosealicense.com/terms")
                .license(license);

        return new OpenAPI().info(info).servers(List.of(localhost, productionServer));
    }
}
