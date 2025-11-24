package com.nox.login.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuramos Spring Security y el filtro JWT
 * Clase principal de configuration de Spring Security
 *
 * Aqui definimos:
 * Que endpoints seran publicos
 * Cuales requieren authentication
 * La politica de sesiones(stateless por JWT)
 * Se registra el filtro JwtFilter
 * Se expone el AuthenticationManager para login
 */
@Configuration
public class SecurityConfig
{
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configura la cadena de seguridad de Spring Security
     * Define como manejara Spring las peticiones HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(csrf -> csrf.disable()) //Desactiva CSRF
                /**
                 * Se desactiva CSRF porque en una API REST con JWT no es necesario
                 * CSRF solo aplica cuando hay sesiones y formularios
                 */
                .authorizeHttpRequests(auth -> auth
                        //Endpoints que NO requieren authentication
                                .requestMatchers("/auth/login", "/auth/register").permitAll() //Endpoints publicos
                                //End//Cualquier otra peticion requiere authentication JWT
                .anyRequest().authenticated() //Las demás requieren authentication
                )
                //Desactiva el formulario de login por defecto de Spring Security
                .formLogin(login -> login.disable()) //Desactiva el formulario
                .httpBasic(basic -> basic.disable()) //Desactiva auth basica
        //No creamos session (JWT es stateless)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //Agregamos el filtro JWT antes del filtro standard de authentication
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Bean del AuthenticationManager
     *  Spring Security lo usa para autenticar al usuario en el login:
     *  Verifica email
     *  Verifica Password
     *  Devuelve UserDetails si es correcto
     *
     *  Es obligatorio tenerlo si vas a usar AuthenticationManager en tu AuthController
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception
    {
        return authConfig.getAuthenticationManager();
    }
}
