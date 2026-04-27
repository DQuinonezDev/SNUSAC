/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import modelo.Estudiante;
import modelo.Instructor;
import modelo.Listas;

/**
 *
 * @author emily
 */
public class ControladorUsuarios {

    private Listas lista;

    public ControladorUsuarios(Listas lista) {
        this.lista = lista;
    }

    // Metodos para INSTRUCTORES
    //agregamos un instructor
    public boolean agregarInstructor(Instructor instructor) {
        // Rechaza si el codigo esta vacio
        if (instructor.getCodigo() == null || instructor.getCodigo().trim().isEmpty()) {
            return false;
        }
        // Rechaza si ya existe un instructor con el mismo codigo
        for (int i = 0; i < lista.cantidadInstructores; i++) {
            if (lista.instructores[i].getCodigo().equals(instructor.getCodigo())) {
                return false;
            }
        }
        // Verifica si el arreglo ya está lleno
        if (lista.cantidadInstructores >= lista.instructores.length) {
            return false;
        }
        // Guarda el instructor
        lista.instructores[lista.cantidadInstructores] = instructor;
        lista.cantidadInstructores++;
        return true;
    }

    public Instructor[] getInstructores() { //obtener el arreglo completo de instructores
        return lista.instructores;
    }

    public int getCantidadInstructores() { //obtener cuantos instructores hay 
        return lista.cantidadInstructores;
    }

    //eliminar un instructor por codigo
    public boolean eliminarInstructor(String codigo) {
        for (int i = 0; i < lista.cantidadInstructores; i++) {     // Recorre todos los instructores existentes
            if (lista.instructores[i].getCodigo().equals(codigo)) {        // Verifica si el código del instructor coincide

                //recore el arreglo y si lo encuentra, mueve todos los elementos hacia la izquierda
                for (int j = i; j < lista.cantidadInstructores - 1; j++) {
                    lista.instructores[j] = lista.instructores[j + 1];
                }

                lista.instructores[lista.cantidadInstructores - 1] = null; // Limpia la ultima posicion despues de eliminar
                lista.cantidadInstructores--;  // Disminuye el contador de instructores

                return true;
            }
        }
        return false;
    }

    public boolean actualizarInstructor(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena) {

        // Recorre todos los instructores existentes
        for (int i = 0; i < lista.cantidadInstructores; i++) {

            // Verifica si el cxodigo coincide con el instructor buscado
            if (lista.instructores[i].getCodigo().equals(codigo)) {

                // Actualiza los datos del instructor
                lista.instructores[i].setNombre(nombre);
                lista.instructores[i].setFechaNacimiento(fechaNacimiento);
                lista.instructores[i].setGenero(genero);
                lista.instructores[i].setContrasena(contrasena);

                return true;
            }
        }

        // Si no se encontró el instructor, retorna false
        return false;
    }

    //MEtodos para ESTUDIANTES 
    // agregar un nuevo estudiante
    public boolean agregarEstudiante(Estudiante estudiante) {
        // Rechaza si el codigo esta vacio
        if (estudiante.getCodigo() == null || estudiante.getCodigo().trim().isEmpty()) {
            return false;
        }
        // Verifica si ya existe un estudiante con el mismo codigo (evita duplicados)
        for (int i = 0; i < lista.cantidadEstudiantes; i++) {
            if (lista.estudiantes[i].getCodigo().equals(estudiante.getCodigo())) {
                return false; // xcodigo repetido
            }
        }
        // Verifica si el arreglo ya esta lleno
        if (lista.cantidadEstudiantes >= lista.estudiantes.length) {
            return false; // No hay espacio
        }

        // Agrega el estudiante en la siguiente posicion libre
        lista.estudiantes[lista.cantidadEstudiantes] = estudiante;

        // Aumenta el contador
        lista.cantidadEstudiantes++;

        return true; // Se agrego correctamente
    }

    //  actualizar los datos de un estudiante
    public boolean actualizarEstudiante(String codigo, String nombre, String fechaNacimiento, String genero, String contrasena) {

        // Recorre todos los estudiantes
        for (int i = 0; i < lista.cantidadEstudiantes; i++) {

            // Busca el estudiante por codigo
            if (lista.estudiantes[i].getCodigo().equals(codigo)) {

                // Actualiza los datos
                lista.estudiantes[i].setNombre(nombre);
                lista.estudiantes[i].setFechaNacimiento(fechaNacimiento);
                lista.estudiantes[i].setGenero(genero);
                lista.estudiantes[i].setContrasena(contrasena);

                return true; // Actualizacion exitosa
            }
        }

        return false; // No se encontro el estudiante
    }

    // eliminar un estudiante por codigo
    public boolean eliminarEstudiante(String codigo) {

        // Recorre todos los estudiantes
        for (int i = 0; i < lista.cantidadEstudiantes; i++) {

            // Busca el estudiante por codigo
            if (lista.estudiantes[i].getCodigo().equals(codigo)) {

                // Mueve todos los elementos una posicion hacia la izquierda
                for (int j = i; j < lista.cantidadEstudiantes - 1; j++) {
                    lista.estudiantes[j] = lista.estudiantes[j + 1];
                }

                // Limpia la ultima posicion
                lista.estudiantes[lista.cantidadEstudiantes - 1] = null;

                // Disminuye el contador
                lista.cantidadEstudiantes--;

                return true; // Eliminado correctamente
            }
        }

        return false; // No se encontro el estudiante
    }

    //  obtener el arreglo completo de estudiantes
    public Estudiante[] getEstudiantes() {
        return lista.estudiantes;
    }

// obtener cuantos estudiantes hay realmente
    public int getCantidadEstudiantes() {
        return lista.cantidadEstudiantes;
    }

    // Carga instructores desde un archivo CSV con formato:
    // Codigo,Nombre,FechaNacimiento,Genero,Contrasena
    // La primera linea se salta porque es el encabezado
    // Las filas con datos invalidos se ignoran silenciosamente
    public void cargarInstructoresCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltamos el encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] partes = linea.split(",");

                // Solo procesamos filas con al menos 5 columnas y codigo no vacio
                if (partes.length >= 5) {
                    String codigo = partes[0].trim();
                    String nombre = partes[1].trim();
                    String fechaNacimiento = partes[2].trim();
                    String genero = partes[3].trim();
                    String contrasena = partes[4].trim();

                    if (!codigo.isEmpty()) {
                        synchronized (this) {
                            agregarInstructor(new Instructor(codigo, nombre, fechaNacimiento, genero, contrasena));
                        }
                    }
                }
            }

            javax.swing.JOptionPane.showMessageDialog(null, "CSV cargado exitosamente.");

        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cargar CSV: " + e.getMessage());
        }
    }

    // Carga estudiantes desde un archivo CSV con formato:
    // Codigo,Nombre,FechaNacimiento,Genero,Contrasena
    // La primera linea se salta porque es el encabezado
    // Las filas con datos invalidos se ignoran silenciosamente
    public void cargarEstudiantesCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltamos el encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] partes = linea.split(",");

                // Solo procesamos filas con al menos 5 columnas y codigo no vacio
                if (partes.length >= 5) {
                    String codigo = partes[0].trim();
                    String nombre = partes[1].trim();
                    String fechaNacimiento = partes[2].trim();
                    String genero = partes[3].trim();
                    String contrasena = partes[4].trim();

                    if (!codigo.isEmpty()) {
                        synchronized (this) {
                            agregarEstudiante(new Estudiante(codigo, nombre, fechaNacimiento, genero, contrasena));
                        }
                    }
                }
            }

            javax.swing.JOptionPane.showMessageDialog(null, "CSV cargado exitosamente.");

        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cargar CSV: " + e.getMessage());
        }
    }
}
