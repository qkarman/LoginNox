package com.nox.login.service;

import com.nox.login.entity.Password;

import java.util.List;

public interface IPasswordService
{
    /**
     * Devuelve una lista de todos el password almacenado en la base de datos
     * @return Lista de password
     */
    List<Password> listarPassword();

    /**
     * Busca y devuelve un password específico por su ID
     * @param idPassword ID del password a buscar
     * @return Password encontrado o null si no existe
     */
    Password buscarPasswordId(Long idPassword);

    /**
     * Guarda un nuevo Password o actualiza uno existente(si ya tiene ID)
     * @param password objeto password a guardar o actualizar
     * @return Password guardado/actualizado
     */
    Password guardarPassword(Password password);

    /**
     * Elimina un Password segun su Id
     * @param idPassword ID del password a eliminar
     */
    void eliminarPassword(Long idPassword);
}
