package Cadastro.mercadoria.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID; // Para gerar nomes de arquivo únicos

@Service
public class FileStorageService {

    @Value("${app.upload.dir}") // Injeta o valor da propriedade do application.properties
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Normaliza o caminho do diretório de upload e o cria se não existir
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // Cria o diretório se ele não existir

        // Gera um nome de arquivo único para evitar colisões e problemas de segurança
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Constrói o caminho completo onde o arquivo será salvo
        Path filePath = uploadPath.resolve(uniqueFileName);

        // Copia o conteúdo do arquivo para o caminho de destino
        Files.copy(file.getInputStream(), filePath);

        // Retorna o caminho relativo (ou o nome único) que será salvo no banco de dados
        // Este caminho será usado para acessar a imagem via URL
        return "/images/products/" + uniqueFileName; // Exemplo: se o acesso for via /images/products/
                                                     // Você pode ajustar isso para "/uploads/images/" + uniqueFileName
                                                     // dependendo de como você configura o acesso estático
    }
}
