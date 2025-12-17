package com.nox.login.evento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_event")
@Getter
@NoArgsConstructor
public class EventoLogin
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identidad observada(no necesariamente valida)
    @Column(nullable = false)
    private String email;

    //Contexto
    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    //Resultado del intento
    @Column(nullable = false)
    private boolean exitoso;

    /**
     * Constructor controlado:
     * un evento nace completo o no nace
     */
    public EventoLogin(String email, String ipAddress, boolean exitoso)
    {
        this.email = email;
        this.ipAddress = ipAddress;
        this.exitoso = exitoso;
        this.timestamp = LocalDateTime.now();
    }
}
