package com.nox.login.repository;

import com.nox.login.entity.Password;
import com.nox.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Nos sirve para hacer communication y poder invocar los DTO
 * Gestiona la persistencia de la entidad {@link Password}
 * Extiende de {@link JpaRepository}, lo que proporciona métodos CRUD
 * listos para usar, como save(), findById(), findAll(), deletedById(), etc.
 *
 * Esta interfaz está pensada para:
 * Buscar passwords asociados a un usuario
 * Integrarse con el sistema de seguridad del proyecto
 * Construir eventualmente filtros para auditorias de acceso
 *
 * Ejemplo de uso:
 * Password password = passwordRepository.findByUser(user)
 */
public interface PasswordRepository extends JpaRepository<Password, Long>
{
    /**
     * Busca una password asociada a un usuario especifico
     *
     * @param user Entidad {@link User} ya cargada previamente
     * @return Objeto {@link Password} asociado a este usuario, o null si no existes
     */
    Password findByUser(User user);
}
