package com.nox.login.service;

import com.nox.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Aquí vamos a desarrollar la logic del usuario, el cual le vamos a implementar validaciones y ver que mas
 */
@Service
public class UserService implements IUserService, UserDetailsService
{
     /*
     Repositorio para acceder a los datos de la entidad User en la base de datos
     La inyección de dependencias es automatic gracias a @Autowired
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Recupera todos los user almacenados en la base de datos
     * @return Lista de todos los user
     */
    @Override
    public List<com.nox.login.entity.User> listarUser()
    {
        return this.userRepository.findAll();
    }

    /**
     * Busca User por su ID
     * @param idUser ID del user a buscar
     * @return User si existe, o null si no se encuentra
     */
    @Override
    public com.nox.login.entity.User buscarUserId(Long idUser)
    {
        return this.userRepository.findById(idUser).orElse(null);
    }

    /**
     * Guarda un nuevo User o actualiza uno existente si ya tiene ID
     * @param user objeto User a guardar o actualizar
     * @return User guardado o actualizado
     */
    @Override
    public com.nox.login.entity.User guardarUser(com.nox.login.entity.User user)
    {
        if(userRepository.existsByEmail(user.getEmail()))
        {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }
        //Encriptamos la password antes de guardar
        user.setPasswordHash(passwordEncoder.encode(user.getPassword().getHash()));
        return this.userRepository.save(user);
    }

    /**
     * Elimina Users de la base de datos por su ID
     * @param idUser ID del user a eliminar
     */
    @Override
    public void eliminarUser(Long idUser)
    {
        this.userRepository.deleteById(idUser);
    }

    // =======================================
    //          Login personalizado
    // =======================================

    /**
     * Creamos la function de validacion de password
     * @param email
     * @param passwordIngresado
     * @return
     */
    public boolean login(String email, String passwordIngresado)
    {
        com.nox.login.entity.User user = userRepository.findByEmail(email).orElse(null);

        if(user == null)
        {
            return false;
        }

        return passwordEncoder.matches(passwordIngresado, user.getPasswordHash());
    }


    // =======================================
    //    Integración con Spring Security
    // =======================================

    /**
     * Spring security lo utiliza para cargar un usuario desde la base de datos
     * a partir del email (que funciona como username del sistema)
     * @param email El email del usuario que intenta autenticarse
     * @return UserDetails objeto que Spring Security utilizara para validar el login
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        //Buscamos usuario por email en la base de datos
        com.nox.login.entity.User user = userRepository.findByEmail(email).orElse(null);

        //Si no se encuentra, lanzamos exception obligatoria de Spring security
        if(user == null)
        {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        /**
         * Convertimos nuestro usuario(entidad propia) en un objeto UserDetails
         * que es el que spring Security necesita para:
         * Obtener username
         * Obtener password
         * Revisar roles/authorities
         *
         * Aqui enviamos:
         * email como username
         * PasswordHash como la password encriptada
         * Una lista vacía de authorities(sin roles)
         */
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}
