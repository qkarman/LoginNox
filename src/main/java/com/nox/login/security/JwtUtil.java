package com.nox.login.security;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Esta clase se encargará de generar el token cuando el user inicie session correctamente y válida
 */
@Component
public class JwtUtil
{
    //Clave secreta(Se debe proteger muy bien, en production la metemos en variable de entorno
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //Duration del token JWT (ejemplo: 1 hora = 3600000 ms)
    private static final long EXPIRATION_TIME = 3600000;

    /**
     * Genera un token JWT con el correo del usuario
     * @param email
     * @return
     */
    public String generarToken(String email)
    {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Valida si el token es correcto y no ha expirado
     */
    public boolean validarToken(String token)
    {
        try
        {
            Claims claims = obtenerClaims(token);
            return claims.getExpiration().after(new Date());
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Obtiene el correo (subject) del usuario desde el token
     *
     */
    public String obtenerEmailDelToken(String token)
    {
        return obtenerClaims(token).getSubject();
    }

    /**
     * Extrae los claims (datos internos) del token
     */
    private Claims obtenerClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
