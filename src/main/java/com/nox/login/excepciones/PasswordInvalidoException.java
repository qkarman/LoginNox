package com.nox.login.excepciones;

public class PasswordInvalidoException extends RuntimeException
{
    public PasswordInvalidoException(String mensaje)
    {
        super(mensaje);
    }
}
