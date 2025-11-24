package com.nox.login.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Esta clase va a servir para crear los atributos del usuario, reforzar seguridad y
 * implementar patrones o algoritmos biomimetics
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;
    private String userName;
    private String email;

    //Creamos una relacion directa con la identidad Password
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private Password password;

    public String getPasswordHash()
    {
        return password != null ? password.getHash() : null;
    }

    public void setPasswordHash(String hash)
    {
        if(this.password == null)
        {
            this.password = new Password();
            this.password.setUser(this);
        }
        this.password.setHash(hash);
    }
}
