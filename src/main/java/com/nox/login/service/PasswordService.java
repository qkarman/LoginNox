package com.nox.login.service;

import com.nox.login.entity.Password;
import com.nox.login.entity.User;
import com.nox.login.excepciones.PasswordInvalidoException;
import com.nox.login.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Aquí vamos a desarrollar la logica del password, el cual le vamos a implementar validaciones de seguridad
 * y logica abstracta pero eficiente
 */
@Service
public class PasswordService implements IPasswordService
{
     /*
     Repositorio para acceder a los datos de la entidad Enemigo en la base de datos
     La inyección de dependencias es automatic gracias a @Autowired
     */
    @Autowired
    private PasswordRepository passwordRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    /**
     * Recupera todos los Passwords almacenados en la base de datos
     * @return Lista a todos los passwords
     */
    @Override
    public List<Password> listarPassword()
    {
        return this.passwordRepository.findAll();
    }

    /**
     * Busca Password por su ID
     * @param idPassword ID del Password a buscar
     * @return Password si existe, o null si no se encuentra
     */
    @Override
    public Password buscarPasswordId(Long idPassword)
    {
        Password password = this.passwordRepository.findById(idPassword).orElse(null);
        return password;
    }

    /**
     * Guarda un nuevo Password o actualiza uno existente si ya tiene ID
     * @param password objeto Password a guardar o actualizar
     * @return Password guardado o actualizado
     */
    @Override
    public Password guardarPassword(Password password)
    {
        return this.passwordRepository.save(password);
    }

    /**
     * Elimina Password de la base de datos por su ID
     * @param idPassword ID del Password a eliminar
     */
    @Override
    public void eliminarPassword(Long idPassword)
    {
        this.passwordRepository.deleteById(idPassword);
    }

    // =======================================
    //     Métodos de validaciones Password
    // =======================================

    /**
     * Aqui vamos a crear la password cifrada
     * @param user
     * @param plainPassword
     * @return
     */
    public Password crearPasswordParaUsuario(User user, String plainPassword)
    {
        validarFortalezaPassword(plainPassword);
        Password password = new Password();
        password.setUser(user);
        password.setHash(encoder.encode(plainPassword)); //Cifrado
        password.setDateCreation(LocalDate.now());
        password.setExpirada(false);
        return password;
    }

    /**
     * Verificamos si la password es correcta
     * @param passwordPlano
     * @param hashGuardado
     * @return
     */
    public boolean verificarPassword(String passwordPlano, String hashGuardado)
    {
        return encoder.matches(passwordPlano, hashGuardado);
    }

    /**
     * Válida la fortaleza del password antes de ser guardada
     *
     * Regla aplicada:
     * Debe tener minimo 8 caracteres
     * Debe incluir al menos 1 letra mayúscula
     * Debe incluir al menos 1 número
     * Debe incluir al menos 1 caracter especial
     * Si alguna regla no se cumple, lanza una PasswordInvalidoException
     */
    private void validarFortalezaPassword(String password)
    {
        //Validamos la longitud minima
        if(password.length() < 8)
        {
            throw new PasswordInvalidoException("La password debe tener al menos 8 caracteres");
        }

        //Validamos de que contenga al menos una letra mayuscula
        if(!password.matches(".*[A-Z].*"))
        {
            throw new PasswordInvalidoException("La password debe contener al menos una letra mayúscula");
        }

        //Validamos de que contenga al menos un numero
        if(!password.matches(".*\\d.*"))
        {
            throw new PasswordInvalidoException("La password debe contener al menos un numero");
        }

        //Validamos de que contenga al menos un caracter especial
        if(!password.matches(".*[!@#$%^&*()_+\\-={}:;\"',.<>?].*"))
        {
            throw new PasswordInvalidoException("La password debe contener al menos un caracter especial");
        }
    }
}
