package com.alura.literAlura.principal;

import com.alura.literAlura.model.dto.Datos;
import com.alura.literAlura.model.dto.DatosLibro;
import com.alura.literAlura.model.entity.Autor;
import com.alura.literAlura.model.entity.Libro;
import com.alura.literAlura.repository.IAutorRepository;
import com.alura.literAlura.repository.ILibroRepository;
import com.alura.literAlura.service.ConsumoApi;
import com.alura.literAlura.service.ConversorImpl;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL = "https://gutendex.com/books/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConversorImpl conversor = new ConversorImpl();
    private Integer opcion = 10;
    private Scanner scanner = new Scanner(System.in);
    private ILibroRepository libroRepository;
    private IAutorRepository autorRepository;

    public Principal(ILibroRepository libroRepository, IAutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    private void leerLibro(Libro libro) {
        System.out.printf("""
                        ------- LIBRO ------
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de descargas: %d
                        -------------------- \n
                        """,
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getIdioma(),
                libro.getNumeroDeDescargas());
    }

    private void buscarLibro() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        scanner.nextLine();
        String nombreLibro = scanner.nextLine();
        String json = consumoApi.obtenerLibros(URL + "?search=" + nombreLibro.replace(" ", "+"));
        List<DatosLibro> libros = conversor.obtenerDatos(json, Datos.class).resultados();
        Optional<DatosLibro> libroOptional = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
        if (libroOptional.isPresent()) {
            var libro = new Libro(libroOptional.get());
            libroRepository.save(libro);
            leerLibro(libro);
        }
        System.out.println("No pudimos encontrar el libro, prueba otro metodo de busqueda.");
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        libros.stream()
                .forEach(this::leerLibro);
    }


    private void leerAutor(Autor autor) {
        System.out.printf("""
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        """,
                autor.getNombre(),
                autor.getFechaDeNacimiento(),
                autor.getFechaDeFallecimiento());

        var libros = autor.getLibros().stream()
                .map(a -> a.getTitulo())
                .collect(Collectors.toList());
        System.out.println("Libros: " + libros + "\n");
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .forEach(this::leerAutor);
    }

    private void listarAutoresPorAño() {
        System.out.print("Ingresa el año en vive o vivió de autor que desea buscar: ");
        Integer anio = scanner.nextInt();
        List<Autor> autores = autorRepository.findByFechaDeFallecimientoGreaterThan(anio);
        autores.stream()
                .forEach(this::leerAutor);
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """);
        String idioma = scanner.next();
        List<Libro> libros = libroRepository.findByIdioma(idioma);
        libros.stream()
                .forEach(this::leerLibro);
    }

    private void generarEstadisticasDelNumeroDeDescargas() {
        var libros = libroRepository.findAll();
        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        for (Libro libro : libros) doubleSummaryStatistics.accept(libro.getNumeroDeDescargas());
        System.out.println("Número de descargas realizadas - " + doubleSummaryStatistics.getCount());
        System.out.println("Número mínimo de descargas - " + doubleSummaryStatistics.getMin());
        System.out.println("Número máximo de descargas - " + doubleSummaryStatistics.getMax());
        System.out.println("Suma total de descargas - " + doubleSummaryStatistics.getSum());
        System.out.println("Promedio del número de descargas - " + doubleSummaryStatistics.getAverage() + "\n");
    }

    private void listarTop10Libros() {
        libroRepository.buscarTop10Libros().stream()
                .forEach(this::leerLibro);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingresa el nombre del autor a buscar");
        scanner.nextLine();
        var nombre = scanner.nextLine();
        autorRepository.findByNombre(nombre).stream()
                .forEach(this::leerAutor);
    }

    public void mostrarMenu() {
        while (opcion != 9) {
            System.out.println("""
                    Elija el número de la opción a ejecutar:
                    1- Buscar libro por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    6- Generar estadísticas del número de descargas
                    7- Listar el top 10 de libros más descargados
                    8- Buscar autor por su nombre
                    9- Salir
                    """);
            opcion = scanner.nextInt();
            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresPorAño();
                case 5 -> listarLibrosPorIdioma();
                case 6 -> generarEstadisticasDelNumeroDeDescargas();
                case 7 -> listarTop10Libros();
                case 8 -> buscarAutorPorNombre();
            }
        }
    }
}
