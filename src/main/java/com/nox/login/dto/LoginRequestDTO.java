package com.nox.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO que recibe las credenciales del usuario al momento de hacer login
 * es utilizado por el controlador {@link com.nox.login.controller.AuthController}
 * durante la authentication de usuarios
 * @Data: Genera automáticamente getters, setters, equals, hashCode y toString.
 * @NoArgsConstructor: Genera un constructor vacío (sin parámetros).
 * @AllArgsConstructor: Genera un constructor con todos los parámetros.
 * @ToString: Sobrescribe el método toString para imprimir los valores de los campos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequestDTO
{
    /**
     * Email del usuario que intenta loguearse
     * Este campo es obligatorio y se envía desde el cliente
     */
    private String email;

    /**
     * Password del usuario para authentication
     * Se envía sin encriptar desde el cliente,
     * pero será validada en el servicio correspondiente
     */
    private String password;
}
