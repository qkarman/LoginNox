package com.nox.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepta peticiones y válido el token
 * Filtro personalizado que intercepta todas las peticiones HTTP
 * para validar el JWT en el encabezado "Authorization".
 *
 * Este filtro se ejecuta una sola vez por request (por eso extiende OncePerRequestFilter)
 * y se encarga de:
 * Extraer el token
 * Validarlo
 * Obtener el usuario
 * Registrar al usuario como autenticado dentro del SecurityContext
 */
@Component
public class JwtFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Metodo principal del filtro, se ejecuta automáticamente para cada peticion
     * @param request peticion entrante del cliente
     * @param response respuesta que se devolvera
     * @param filterChain cadena de filtros que deben ejecutar tras este
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        //1. Leemos el header Authorization donde debe venir el token
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;

        //2. Verificamos que el header no este vació y empiece con bearer
        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            //Extraemos el token quitando los primeros 7 caracteres(Bearer)
            jwtToken = authHeader.substring(7);

            //Obtenemos el email (username) con el que se generó el token
            email = jwtUtil.obtenerEmailDelToken(jwtToken);
        }

        /**
         * 3. Validamos el token solo si:
         * Se extrajo un email del toke
         * y aún no hay un usuario autenticado en el contexto
         *
         * Esto evita volver a auténtica a un usuario que ya está autenticado
         */
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            //Cargamos el usuario desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //Validamos que el token no este expirado ni alterado
            if (jwtUtil.validarToken(jwtToken))
            {
                /**
                 * Creamos un objeto Authentication válido para Spring Security
                 * Incluye:
                 * El usuario
                 * Sus roles(authorities)
                 * No necesita password porque ya fue autenticado por el token
                 */
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken
                                (userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                //Registramos la authentication en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //4. Dejamos continuar la peticion al siguiente filtro o al controlador
        filterChain.doFilter(request, response);
    }
}
