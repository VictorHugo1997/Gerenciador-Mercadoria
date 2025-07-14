package Cadastro.mercadoria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import Cadastro.mercadoria.models.User;

public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByLogin(String login); // Precisa retornar UserDetails que é o contrato da classe User
}
