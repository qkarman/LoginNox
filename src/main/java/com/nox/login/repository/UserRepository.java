package com.nox.login.repository;

import com.nox.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Nos sirve para hacer communication y poder invocar los DTO
 * Repositorio encargado de acceder y gestionar los datos de la entidad User
 * en la base de datos. Extiende JpaRepository, lo que nos da acceso a
 * operaciones CRUD listas para usar (crear, leer, actualizar, eliminar).
 *
 * También nos permite definir métodos personalizados siguiendo las convenciones
 * de nombres de Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<User, Long>
{
    /**
     * Busca un usuario por su correo electronico
     *
     * Spring Data JPA interpreté el nombre del método y automáticamente
     * genera la consulta SQL necesaria
     * @param email correo electronico del usuario a buscar
     * @return Optional<User> - Contiene el usuario si existe, vació si no</User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si un usuario ya está registrado en el sistema usando su email.
     *
     * Muy util para validar antes de crear un nuevo usuario y evitar correos duplicados
     * @param email Correo electronico a verificar
     * @return true si existe un usuario registrado con ese email, false si no
     */
    boolean existsByEmail(String email);
}
