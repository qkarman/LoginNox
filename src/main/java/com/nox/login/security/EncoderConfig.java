package com.nox.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase de configuration encargada de exponer un bean para encriptar passwords
 *
 * Spring Security requiere un PasswordEncoder para codificar y validar
 * passwords, En este caso, usamos BCrypt, uno de los algoritmos más seguros
 * y recomendados debido a que aplica "salting" y es resistente a ataques de fuerza bruta
 */
@Configuration
public class EncoderConfig
{
    /**
     * Bean que provee una instancia de BCryptPasswordEncoder
     *
     * Este objeto se inyecta automáticamente donde sea necesario
     * (por ejemplo en servicios de registro o authentication).
     *
     * @return instancia de BCryptPasswordEncoder encargada de encriptar passwords
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}

