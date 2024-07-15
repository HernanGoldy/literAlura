package com.alura.literAlura.model.entity;

import com.alura.literAlura.model.dto.DatosAutor;
import com.alura.literAlura.model.dto.DatosLibro;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Autor autor;
    private String idioma;
    @Column(name = "numero_de_descargas")
    private Integer numeroDeDescargas;

    public Libro(DatosLibro libro) {
        this.titulo = libro.titulo();
        Optional<DatosAutor> autor = libro.autores().stream()
                .findFirst();
        if (autor.isPresent()) {
            this.autor = new Autor(autor.get());
        } else {
            System.out.println("No se encontró el autor");
        }
        this.idioma = libro.idiomas().get(0);
        this.numeroDeDescargas = libro.numeroDeDescargas();
    }
}
