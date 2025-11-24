package com.nox.login.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Entidad que representa el password o credencial de acceso de un usuario
 * Se relaciona con la entidad {@link User} mediante una relacion uno a uno
 *
 * Este objeto se almacena:
 * El hash del password
 * la fecha de creation
 * Un indicador para saber si está expirado
 *
 * Esta entidad puede servir para:
 * Control de caducidad de passwords
 * Auditorias de seguridad
 * Integration con módulos de recuperation o cambios de passwords
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Password
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPassword; //Identificador unico del password en BD
    private String hash;  //Hash seguro del password(No se guarda en texto plano)
    private LocalDate dateCreation; //Fecha en la que se creó o se actualizó el password
    private boolean expirada; //Flag que indica si la password ya no es valida

    //Creamos una relacion directa con la identidad user
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonBackReference
    @ToString.Exclude
    private User user;
}
