package com.nox.login.service;

import com.nox.login.entity.User;

import java.util.List;

/**
 * Interfaz que define la gestion de la entidad user
 * Define las funciones basics del proyecto CRUD sin importar la implementation
 */
public interface IUserService
{
    /**
     * Devuelve una lista de todos los usuarios almacenados en la base de datos
     * @return Lista de users
     */
    List<User> listarUser();

    /**
     * Busca y devuelve un user especifico por su ID
     * @param idUser ID del user a buscar
     * @return User encontrado o null si no existe
     */
    User buscarUserId(Long idUser);

    /**
     * Guarda un nuevo user o actualiza uno existente(si ya tiene ID)
     * @param user objeto user a guardar o actualizar
     * @return User guardado/actualizado
     */
    User guardarUser(User user);

    /**
     * Elimina un user segun su Id
     * @param idUser ID del user a eliminar
     */
    void eliminarUser(Long idUser);

}
