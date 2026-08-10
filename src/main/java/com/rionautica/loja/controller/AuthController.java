package com.rionautica.loja.controller;

import com.rionautica.loja.model.Usuario;
import com.rionautica.loja.repository.UsuarioRepository;
import com.rionautica.loja.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder
                          ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow();

        String token = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token, usuario.getNome(), usuario.getPerfil().name()));
    }


    @GetMapping("/gerar-hash")
    public ResponseEntity<String> gerarHash(@RequestParam String senha) {
        return ResponseEntity.ok(passwordEncoder.encode(senha));
    }

    record LoginRequest(String login, String senha) {}
    record LoginResponse(String token, String nome, String perfil) {}


}