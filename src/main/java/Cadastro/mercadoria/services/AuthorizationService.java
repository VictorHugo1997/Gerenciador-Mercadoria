package Cadastro.mercadoria.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Cadastro.mercadoria.repositories.UserRepository;

/**
 * Aqui poderiamos consultar os dados de login em outra API ou no google por
 * exemplo
 * Mas aqui vamos pegar no nosso banco mesmo
 */
@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
