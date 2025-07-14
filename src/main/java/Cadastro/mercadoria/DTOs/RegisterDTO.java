package Cadastro.mercadoria.DTOs;

import Cadastro.mercadoria.models.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {

}
