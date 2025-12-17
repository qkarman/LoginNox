package com.nox.login.repository;

import com.nox.login.evento.EventoLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public  interface EventoLoginRepository extends JpaRepository<EventoLogin, Long>
{
    //Eventos recientes por IP
    List<EventoLogin> findByIpAddressAndTimestampAfter(String ipAddress, LocalDateTime timestamp);

    //Eventos recientes por email
    List<EventoLogin> findByEmailAndTimestampAfter(String email, LocalDateTime timestamp);

    //Conteo de intentos fallidos recientes por IP
    @Query("""
            SELECT COUNT(e)
            FROM EventoLogin e
            WHERE e.ipAddress = :ip
            AND e.exitoso = false
            AND e.timestamp >= :desde
            """)
    long contarFallosRecientesPorIp(String ip, LocalDateTime desde);

}
