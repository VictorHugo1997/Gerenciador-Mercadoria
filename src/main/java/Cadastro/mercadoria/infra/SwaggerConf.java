package Cadastro.mercadoria.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConf {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gerenciador de mercadorias")
                        .version("1.0")
                        .description(
                                "API para gerenciamento de mercadorias, cadastro, edição e exclusão de mercadorias"));
    }
}
