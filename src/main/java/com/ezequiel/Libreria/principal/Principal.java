package com.ezequiel.Libreria.principal;

import com.ezequiel.LibreriaAlura.modelos.Autor;
import com.ezequiel.LibreriaAlura.modelos.Datos;
import com.ezequiel.LibreriaAlura.modelos.DatosAutor;
import com.ezequiel.LibreriaAlura.modelos.DatosLibros;
import com.ezequiel.LibreriaAlura.modelos.Libro;
import com.ezequiel.LibreriaAlura.repository.AutorRepository;
import com.ezequiel.LibreriaAlura.repository.LibroRepository;
import com.ezequiel.LibreriaAlura.service.ConsumoApi;
import com.ezequiel.LibreriaAlura.service.ConvierteDatos;

import java.util.*;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private List<Autor> autores;
    private List<Libro> libros;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {

        var opcion = -10;
        try {
            while (opcion != 0) {
                var menu = """
                                            
                        1) Buscar Libro
                        2) Historial libros
                        3) Historial autores
                        4) Lista de autores vivos
                        5) Listar LIBROS por IDIOMA
                                            
                        0) Salir
                                            
                        Ingresa una de las opciones:
                        """;
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listaDeLibrosRegistrados();
                        break;
                    case 3:
                        listaDeAutoresRegistrados();
                        break;
                    case 4:
                        listaDeAutoresVivosEnDeterminadoAnio();
                        break;
                    case 5:
                        listaDeLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;

                }
            }
        } catch (InputMismatchException e) {
            System.out.println("ERROR! ");
        }
    }

    private Datos buscarDatosLibros() {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        var libro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + libro.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private Libro agregarLibroBD(DatosLibros datosLibros, Autor autor) {
        Libro libro = new Libro(datosLibros, autor);
        return libroRepository.save(libro);

    }

    private void buscarLibroPorTitulo() {
        Datos datos = buscarDatosLibros();

        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibros.autor().get(0);
            Libro libroBuscado = libroRepository.findByTituloIgnoreCase(datosLibros.titulo());

            if (libroBuscado != null) {
                System.out.println(libroBuscado);
                System.out.println("No se puede volver a registrar.");

            } else {
                Autor autorBuscado = autorRepository.findByNombreIgnoreCase(datosAutor.nombre());

                if (autorBuscado == null) {
                    Autor autor = new Autor(datosAutor);
                    autorRepository.save(autor);
                    Libro libro = agregarLibroBD(datosLibros, autor);
                    System.out.println(libro);
                } else {
                    Libro libro = agregarLibroBD(datosLibros, autorBuscado);
                    System.out.println(libro);
                }
            }
        } else {
            System.out.println("ERROR!! Pruebe con otro.");
        }
    }

    private void listaDeLibrosRegistrados() {
        libros = libroRepository.findAll();
        if (!libros.isEmpty()) {
            libros.stream().forEach(System.out::println);
        } else {
            System.out.println("No tenemos resgistros");
        }
    }

    private void listaDeAutoresRegistrados() {
        autores = autorRepository.findAll();
        if (!autores.isEmpty()) {
            autores.stream().forEach(System.out::println);
        } else {
            System.out.println("No tenemos resgistros");
        }
    }

    private void listaDeAutoresVivosEnDeterminadoAnio() {
        System.out.println("Ingrese el año en el que quiere saber los autores vivos: ");
        String fecha = teclado.nextLine();
        try {
            List<Autor> autoresVivosEnCiertaFecha = autorRepository.autorVivoEnDeterminadoAnio(fecha);
            if (!autoresVivosEnCiertaFecha.isEmpty()) {
                autoresVivosEnCiertaFecha.stream().forEach(System.out::println);
            } else {
                System.out.println("No existen Autores vivos en esos años.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void listaDeLibrosPorIdioma() {
        System.out.println("""
                1) Español (ES)
                2) Inglés (EN)
                3) Francés (FR)
                4) Portugués (PT)
                                
                5) Regresar al menú principal
                                
                Por favor, ingrese el número de opción para elegir el idioma de los libros a consultar:
                """);
        int opcion;
        opcion = teclado.nextInt();
        teclado.nextLine();
        switch (opcion) {
            case 1:
                libros = libroRepository.findByIdiomasContaining("es");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Español.");
                }
                break;
            case 2:
                libros = libroRepository.findByIdiomasContaining("en");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Inglés.");
                }
                break;
            case 3:
                libros = libroRepository.findByIdiomasContaining("fr");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Francés.");
                }
                break;
            case 4:
                libros = libroRepository.findByIdiomasContaining("pt");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Portugués.");
                }
                break;
            case 5:
                muestraElMenu();
                break;
            default:
                System.out.println("La opción seleccionada no es válida.");
        }
    }
}