/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.BufferedReader;
import java.io.FileReader;
import modelo.Curso;
import modelo.Listas;
/**
 *
 * @author emily
 */
public class ControladorCursos {

    // Referencia al modelo central que contiene todos los datos del sistema
    private Listas lista;

    public ControladorCursos(Listas lista) {
        this.lista = lista;
    }

 
     //Agrega un nuevo curso al sistema si el codigo no existe y hay espacio.
 
    public boolean agregarCurso(Curso curso) {
        // Rechaza si el codigo esta vacio
        if (curso.getCodigo() == null || curso.getCodigo().trim().isEmpty()) {
            return false;
        }
        // Verifica que no exista ya un curso con el mismo codigo
        for (int i = 0; i < lista.cantidadCursos; i++) {
            if (lista.cursos[i].getCodigo().equals(curso.getCodigo())) {
                return false; // Codigo duplicado
            }
        }

        // Verifica que el arreglo no este lleno
        if (lista.cantidadCursos >= lista.cursos.length) {
            return false; // No hay espacio
        }

        // Guarda el curso en la siguiente posicion libre
        lista.cursos[lista.cantidadCursos] = curso;
        lista.cantidadCursos++; // Incrementa el contador

        return true;
    }


    //actualar cursos
    public boolean actualizarCurso(String codigo, String nombre, String descripcion, int creditos, String seccion) {
        // Recorre todos los cursos registrados
        for (int i = 0; i < lista.cantidadCursos; i++) {

            // Busca el curso por codigo
            if (lista.cursos[i].getCodigo().equals(codigo)) {

                // Actualiza solo los campos modificables
                lista.cursos[i].setNombre(nombre);
                lista.cursos[i].setDescripcion(descripcion);
                lista.cursos[i].setCreditos(creditos);
                lista.cursos[i].setSeccion(seccion);

                return true; // Actualizacion exitosa
            }
        }

        return false; // No se encontro el curso
    }


     //Elimina un curso por codigo, desplazando el arreglo hacia la izquierda
    
    public boolean eliminarCurso(String codigo) {
        // Recorre todos los cursos
        for (int i = 0; i < lista.cantidadCursos; i++) {

            // Busca el curso por codigo
            if (lista.cursos[i].getCodigo().equals(codigo)) {

                // Corre todos los elementos una posicion hacia la izquierda
                for (int j = i; j < lista.cantidadCursos - 1; j++) {
                    lista.cursos[j] = lista.cursos[j + 1];
                }

                // Limpia la ultima posicion que quedo duplicada
                lista.cursos[lista.cantidadCursos - 1] = null;

                // Disminuye el contador
                lista.cantidadCursos--;

                return true; // Eliminado correctamente
            }
        }

        return false; // No se encontro el curso
    }

    public Curso[] getCursos() {
        return lista.cursos;
    }

    public int getCantidadCursos() {
        return lista.cantidadCursos;
    }

    // Asigna un instructor a un curso buscandolo por su codigo
    // Si se pasa una cadena vacia como codigoInstructor, se quita el instructor
    public boolean asignarInstructor(String codigoCurso, String codigoInstructor) {
        // Recorre todos los cursos para encontrar el que coincide
        for (int i = 0; i < lista.cantidadCursos; i++) {
            if (lista.cursos[i].getCodigo().equals(codigoCurso)) {
                // Guarda el codigo del instructor en el curso encontrado
                lista.cursos[i].setCodigoInstructor(codigoInstructor);
                return true; // Se asigno correctamente
            }
        }
        return false; // No se encontro ningun curso con ese codigo
    }

    // Carga cursos desde un archivo CSV con formato:
    // Codigo,Nombre,Descripcion,Creditos,Seccion
    // La primera linea se salta porque es el encabezado
    // Las filas con datos invalidos se ignoran silenciosamente
    public void cargarCursosCSV(String rutaArchivo) {
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
                    String descripcion = partes[2].trim();
                    String creditosTexto = partes[3].trim();
                    String seccion = partes[4].trim();

                    if (!codigo.isEmpty()) {
                        try {
                            int creditos = Integer.parseInt(creditosTexto);
                            synchronized (this) {
                                agregarCurso(new Curso(codigo, nombre, descripcion, creditos, seccion));
                            }
                        } catch (NumberFormatException e) {
                            // Fila con creditos no numericos, la saltamos
                        }
                    }
                }
            }

            javax.swing.JOptionPane.showMessageDialog(null, "CSV cargado exitosamente.");

        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cargar CSV: " + e.getMessage());
        }
    }
}
