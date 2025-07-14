package Cadastro.mercadoria.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Cadastro.mercadoria.DTOs.AuthenticationDTO;
import Cadastro.mercadoria.DTOs.LoginResponseDTO;
import Cadastro.mercadoria.DTOs.RegisterDTO;
import Cadastro.mercadoria.models.User;
import Cadastro.mercadoria.repositories.UserRepository;
import Cadastro.mercadoria.services.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    TokenService tokenService;
    // fornecido pelo security para autenticar
    @Autowired
    private AuthenticationManager authenticationManager; // na classe SecurityConf definimos ela

    @Autowired
    private UserRepository repository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        // Essa calsse UsernamePass é padrão do security
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        // esse authentication recebe como parametro login e senha juntos, no caso o
        // userName que armazenou os dois em uma variável
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.genereteToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByLogin(data.login()) != null)
            return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
