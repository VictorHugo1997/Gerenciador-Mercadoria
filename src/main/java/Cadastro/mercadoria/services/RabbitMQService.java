package Cadastro.mercadoria.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendStockZeroMessage(String produtoName) {
        System.out.println("Enviando mensagem para a fila: " + produtoName);
        rabbitTemplate.convertAndSend("stock-zero-queue", produtoName);
    }
}
