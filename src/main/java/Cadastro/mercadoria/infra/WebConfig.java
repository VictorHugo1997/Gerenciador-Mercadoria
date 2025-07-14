package Cadastro.mercadoria.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir; // O mesmo diretório configurado em application.properties

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia "/images/products/**" para o diretório de upload
        // Note o "file:/" para indicar que é um recurso do sistema de arquivos
        registry.addResourceHandler("/images/products/**")
                .addResourceLocations("file:" + uploadDir + "/"); // Adicione a barra final para que funcione
                                                                  // corretamente
    }
}