package com.nox.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginApplication
{
	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}
}

//Debemos ver los endpoints y cambiar el hash por raw y de ahi analizar las peticiones para poder
//Implementar el algoritmo biomimetic