package com.rionautica.loja.security;

import com.rionautica.loja.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();

    private SecretKey getKey() {
        return KEY;
    }

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getLogin())
                .claim("nome", usuario.getNome())
                .claim("perfil", usuario.getPerfil().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(KEY)
                .compact();
    }

    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, Usuario usuario) {
        return extrairLogin(token).equals(usuario.getLogin())
                && !extrairClaims(token).getExpiration().before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}