package com.nox.login.controller;

import com.nox.login.entity.Password;
import com.nox.login.entity.User;
import com.nox.login.excepciones.RecursoNoEncontradoExcepcion;
import com.nox.login.repository.PasswordRepository;
import com.nox.login.repository.UserRepository;
import com.nox.login.service.PasswordService;
import com.nox.login.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con los Users de la cuenta
 * proporciona endpoints para realizar operaciones CRUD y filtros personalizados
 *
 * Ruta base: <a href="http://localhost:8080/login-app">...</a>
 *Ejemplo de uso:
 * - Listar users: GET /login-app/users
 * - Obtener user por ID: GET /login-app/users/{id}
 * - Crear user: POST /login-app/users
 * - Actualizar user: PUT /login-app/users/{id}
 * - Eliminar user: DELETE /login-app/users/{id}
 * - Filtros de seguridad:
 */
@RestController //Indica que esta clase es un controlador REST
//Ruta base para acceder a los endpoints
@RequestMapping("login-app") //http://localhost:8080/login-app/user
public class UserController
{
    //Creamos el método para imprimir información util para debugging
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene todos los users registrados en la BD
     * @return Lista de objetos {@link User}
     */
    @GetMapping("/users") //http://localhost:8080/login-app/users
    public List<User> obtenerUsers()
    {
        List<User> users = this.userService.listarUser();
        log.info("Users obtenidos: ");
        users.forEach(user -> log.info(user.toString()));
        return users;
    }

    /**
     * Crea un nuevo User y su password cifrado en el sistema
     * @param user objeto {link User} recibiendo en el cuerpo de la peticion
     * @return el user guardado
     */
    @PostMapping("/users")
    public User agregarUser(@RequestBody User user)
    {
        log.info("User a agregar: {}",  user.getUserName());
        return userService.crearUsuario(user);
    }

    /**
     * Busca un user por su ID
     * @param id identificador unico del user
     * @return {@link ResponseEntity} con el user si existe
     * @throws RecursoNoEncontradoExcepcion si no encuentra el user
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> obtenerUserId(@PathVariable Long id)
    {
        User user = this.userService.buscarUserId(id);
        if(user != null)
        {
            return ResponseEntity.ok(user);
        }
        else
        {
            throw new RecursoNoEncontradoExcepcion("No se encontró el id: " + id);
        }
    }

    /**
     * Actualiza los datos de un user existente
     * @param id identificador del user a actualizar
     * @param userRecibido datos actualizados en el cuerpo de la peticion
     * @return {@link ResponseEntity} con el user actualizado
     */
    @PutMapping("/Users/{id}")
    public ResponseEntity<User> actualizarUser(@PathVariable Long id, @RequestBody User userRecibido)
    {
        User actualizado = userService.actualizarUsuario(id, userRecibido);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un user de la BD
     * @param id identificador del user a eliminar
     * @return {@link ResponseEntity} con confirmacion de eliminacion
     * @throws RecursoNoEncontradoExcepcion si el user no existe
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarUser(@PathVariable Long id)
    {
        User user = this.userService.buscarUserId(id);

        if(user == null)
        {
            throw new RecursoNoEncontradoExcepcion(" No se encontró el id: " + id);
        }

        this.userService.eliminarUser(user.getIdUser());
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    //   Métodos de endpoints para filtros de seguridad User
    // =========================================================

    /*
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest)
    {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if(optionalUser.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        User user = optionalUser.get(); // ahora si invocamos el objeto real

        Password password = passwordRepository.findByUser(user);
        boolean match = passwordEncoder.matches(loginRequest.getPassword(), password.getHash());

        if (match)
        {
            return ResponseEntity.ok("Login exitoso");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
    }*/
}
