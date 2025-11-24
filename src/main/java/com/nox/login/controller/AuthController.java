package com.nox.login.controller;

import com.nox.login.dto.LoginRequestDTO;
import com.nox.login.entity.User;
import com.nox.login.security.JwtUtil;
import com.nox.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Este controlador maneja la authentication de usuarios y la generation de tokens JWT
 */
@RestController
@RequestMapping("/auth") //Prefijo del endpoint
public class AuthController
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * Endpoint de login - genera y devuelve un JWT
     * válida credencial y genera un JWT si es válido
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDTO loginRequest)
    {
        //Autenticamos usuario
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        //Si llega aquí, authentication fue exitosa
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generarToken(userDetails.getUsername());

        //Respuesta con token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login exitoso");
        return response;
    }

    /**
     * Endpoint de registro de nuevo usuario
     */
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User newUser)
    {
        userService.guardarUser(newUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");
        return response;
    }
}
