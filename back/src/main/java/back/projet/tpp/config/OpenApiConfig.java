package back.projet.tpp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TPP API")
                        .version("v1")
                        .description("TPP (Practical Work) management API - Manages students, supervisors, courses and time slots")
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Local development server")
                );
    }
}
