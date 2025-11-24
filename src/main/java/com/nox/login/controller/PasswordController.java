package com.nox.login.controller;

import com.nox.login.entity.Password;
import com.nox.login.excepciones.RecursoNoEncontradoExcepcion;
import com.nox.login.service.PasswordService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con el password de la cuenta
 * proporciona endpoints para realizar operaciones CRUD y filtros personalizados
 * Ruta base: "<a href="http://localhost8080/login-app">...</a>"
 * Ejemplo de uso:
 * - Listar Passwords: GET
 */
//!Seguir documentando y probando endpoints
@RestController //Indica que esta clase es un constructor REST
//Ruta base para acceder a los endpoints
@RequestMapping("password") //http://localhost:8080/password/passwords
public class PasswordController
{
    //Creamos el método para imprimir information util para debugging
    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private PasswordService passwordService;

    /**
     * Obtiene todos los passwords registrados en la BD
     * @return Lista de objetos {@link Password}
     */
    @GetMapping("/passwords") //http://localhost:8080/password/passwords
    public List<Password> obtenerPassword()
    {
        List<Password> passwords = this.passwordService.listarPassword();
        log.info(" Passwords obtenidos: ");
        passwords.forEach(password -> log.info(password.toString()));
        return passwords;
    }

    /**
     * Crea un nuevo Password en el sistema
     * @param password objeto {link password} recibiendo en el cuerpo de la peticion
     * @return el password guardado
     */
    @PostMapping("/passwords")
    public Password agregarPassword(@RequestBody Password password)
    {
        log.info("passwords a agregar: " + password);
        return this.passwordService.guardarPassword(password);
    }

    /**
     * Busca un passwords por su id
     * @param id identificador unico del password
     * @return {@link ResponseEntity} con el password si existe
     * @throws RecursoNoEncontradoExcepcion si no encuentra el password
     */
    @GetMapping("/password/{id}")
    public ResponseEntity<Password> obtenerPasswordId(@PathVariable Long id)
    {
        Password password = this.passwordService.buscarPasswordId(id);
        if(password != null)
        {
            return ResponseEntity.ok(password);
        }
        else
        {
            throw new RecursoNoEncontradoExcepcion("No se encontró el id del password: " + id);
        }
    }

    /**
     * Actualiza los datos de un password existente
     * @param id identificador del password a actualizar
     * @param passwordRecibido datos actualizados en el cuerpo de la peticion
     * @return {@link ResponseEntity} con el password actualizado
     */
    @PutMapping("/password/{id}")
    public ResponseEntity<Password> actualizarPassword(@PathVariable Long id, @RequestBody Password passwordRecibido)
    {
        Password password = this.passwordService.buscarPasswordId(id);
        password.setHash(passwordRecibido.getHash());
        password.setDateCreation(passwordRecibido.getDateCreation());

        //Pendiente ---------------------------------------------------------
        //Guardamos la information
        this.passwordService.guardarPassword(password);
        return ResponseEntity.ok(password);
    }

    /**
     * Elimina un password de la BD
     * @param id identificador del password a eliminar
     * @return {@link ResponseEntity} con confirmacion de eliminacion
     * @throws RecursoNoEncontradoExcepcion si el password no existe
     */
    @DeleteMapping("/password/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarPassword(@PathVariable Long id)
    {
        Password password = this.passwordService.buscarPasswordId(id);

        if(password == null)
        {
            throw new RecursoNoEncontradoExcepcion(" No se encontró el id del password: " + id);
        }

        this.passwordService.eliminarPassword(password.getIdPassword());
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    // =========================================================================
    //   Métodos de endpoints para filtros de seguridad y funciones Password
    // =========================================================================
}
