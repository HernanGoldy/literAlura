package com.alura.literAlura.repository;

import com.alura.literAlura.model.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAutorRepository extends JpaRepository<Autor, Long> {

    List<Autor> findByFechaDeFallecimientoGreaterThan(Integer anio);

    List<Autor> findByNombre(String nombre);
}
