package com.ezequiel.LibreriaAlura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezequiel.LibreriaAlura.modelos.Autor;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Autor findByNombreIgnoreCase(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :fecha AND a.fechaDeFallecimiento >= :fecha")
    List<Autor> autorVivoEnDeterminadoAnio(@Param("fecha") String fecha);
}