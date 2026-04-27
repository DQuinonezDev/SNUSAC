/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import modelo.Listas;
import modelo.Nota;

/**
 *
 * @author emily
 */
public class ControladorNotas {


    private Listas lista;

    // Constructor
    public ControladorNotas(Listas lista) {
        this.lista = lista;
    }

    // Busca en el arreglo de estudiantes si existe uno con ese codigo.
    // Devuelve true si lo encuentra, false si no.
    private boolean estudianteExiste(String codigoEstudiante) {
        for (int i = 0; i < lista.cantidadEstudiantes; i++) {
            if (lista.estudiantes[i].getCodigo().equals(codigoEstudiante)) {
                return true;
            }
        }
        return false;
    }

    // Agrega una nota
    // Valida que no exista ya una nota para la misma combinacion d curso seccion estudiante (eso seria duplicado).
    // Tambien valida que la ponderacion sea positiva y la nota este entre 0 y 100.
    public boolean agregarNota(Nota nota) {
        // Revisamos que la ponderacion sea mayor a 0
        if (nota.getPonderacion() <= 0) {
            return false;
        }

        // Revisamos que la nota este en rango valido
        if (nota.getNota() < 0 || nota.getNota() > 100) {
            return false;
        }

        // Revisamos que el estudiante exista en el sistema antes de registrar su nota
        if (!estudianteExiste(nota.getCodigoEstudiante())) {
            return false; // El codigo de estudiante no corresponde a nadie registrado
        }

        // Revisamos que no exista ya una nota con la misma combinacion
        for (int i = 0; i < lista.cantidadNotas; i++) {
            Nota n = lista.notas[i];
            if (n.getCodigoCurso().equals(nota.getCodigoCurso())
                    && n.getCodigoSeccion().equals(nota.getCodigoSeccion())
                    && n.getCodigoEstudiante().equals(nota.getCodigoEstudiante())) {
                return false; // Ya existe una nota para este estudiante 
            }
        }

        // Revisamos que haya espacio en el arreglo
        if (lista.cantidadNotas >= lista.notas.length) {
            return false; // El arreglo esta lleno
        }

        // Guardamos la nota y aumentamos el contador
        lista.notas[lista.cantidadNotas] = nota;
        lista.cantidadNotas++;
        return true;
    }

    // Actualiza la ponderacion, la nota y la fecha de una nota existente.
    // Para buscarla necesitamos los tres codigos curso, seccion, estudiante
    public boolean actualizarNota(String codigoCurso, String codigoSeccion, String codigoEstudiante,
            double nuevaPonderacion, double nuevaNota, String nuevaFecha) {
        // Validamos que los nuevos valores sean correctos antes de buscar
        if (nuevaPonderacion <= 0) {
            return false;
        }
        if (nuevaNota < 0 || nuevaNota > 100) {
            return false;
        }

        // Buscamos la nota que coincida con los tres codigos
        for (int i = 0; i < lista.cantidadNotas; i++) {
            Nota n = lista.notas[i];
            if (n.getCodigoCurso().equals(codigoCurso)
                    && n.getCodigoSeccion().equals(codigoSeccion)
                    && n.getCodigoEstudiante().equals(codigoEstudiante)) {
                // La encontramos, actualizamos sus valores
                n.setPonderacion(nuevaPonderacion);
                n.setNota(nuevaNota);
                n.setFechaRegistro(nuevaFecha);
                return true;
            }
        }

        return false; // No se encontro la nota
    }

    // Elimina una nota buscandola por los tres codigos.
    public boolean eliminarNota(String codigoCurso, String codigoSeccion, String codigoEstudiante) {
        for (int i = 0; i < lista.cantidadNotas; i++) {
            Nota n = lista.notas[i];
            if (n.getCodigoCurso().equals(codigoCurso)
                    && n.getCodigoSeccion().equals(codigoSeccion)
                    && n.getCodigoEstudiante().equals(codigoEstudiante)) {
                // Corremos todos los elementos una posicion hacia la izquierda
                for (int j = i; j < lista.cantidadNotas - 1; j++) {
                    lista.notas[j] = lista.notas[j + 1];
                }
                // Limpiamos la ultima posicion que quedo repetida
                lista.notas[lista.cantidadNotas - 1] = null;
                lista.cantidadNotas--;
                return true;
            }
        }

        return false; // No se encontro la nota
    }


    // Devuelve el arreglo completo de notas (para que la tabla pueda leerlo)
    public Nota[] getNotas() {
        return lista.notas;
    }

    // Devuelve cuantas notas hay guardadas
    public int getCantidadNotas() {
        return lista.cantidadNotas;
    }

    // Calcula el promedio ponderado de un estudiante en una seccion especifica.
    // Formula: suma(nota * ponderacion) / suma(ponderacion)
    public double calcularPromedio(String codigoEstudiante, String codigoSeccion) {
        double sumaNumerador = 0;   // Acumulamos nota * ponderacion
        double sumaPonderaciones = 0; // Acumulamos todas las ponderaciones

        for (int i = 0; i < lista.cantidadNotas; i++) {
            Nota n = lista.notas[i];
            if (n.getCodigoEstudiante().equals(codigoEstudiante)
                    && n.getCodigoSeccion().equals(codigoSeccion)) {
                sumaNumerador += n.getNota() * n.getPonderacion();
                sumaPonderaciones += n.getPonderacion();
            }
        }

        // Si no encontramos ninguna nota, devolvemos -1 como señal de "no hay datos"
        if (sumaPonderaciones == 0) {
            return -1;
        }

        return sumaNumerador / sumaPonderaciones;
    }
    // Carga notas desde un archivo CSV.
    // Formato: CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,Fecha
    // La primera linea se salta porque es el encabezado.
    // Las filas con datos invalidos se ignoran silenciosamente.
    public void cargarNotasCSV(String rutaArchivo) {
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

                // Solo procesamos filas con al menos 6 columnas
                if (partes.length >= 6) {
                    String codigoCurso = partes[0].trim();
                    String codigoSeccion = partes[1].trim();
                    String codigoEstudiante = partes[2].trim();
                    String ponderacionTexto = partes[3].trim();
                    String notaTexto = partes[4].trim();
                    String fecha = partes[5].trim();

                    try {
                        double ponderacion = Double.parseDouble(ponderacionTexto);
                        double nota = Double.parseDouble(notaTexto);
                        // Usamos synchronized para que sea seguro si se llama desde un hilo
                        synchronized (this) {
                            agregarNota(new Nota(codigoCurso, codigoSeccion, codigoEstudiante, ponderacion, nota, fecha));
                        }
                    } catch (NumberFormatException e) {
                        // Fila con datos no numericos, la saltamos
                    }
                }
            }

            javax.swing.JOptionPane.showMessageDialog(null, "CSV cargado exitosamente.");

        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cargar CSV: " + e.getMessage());
        }
    }

    // Exportar todas las notas del sistema a un archivo CSV.
    public String exportarNotasCSV(String rutaArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            // Escribimos encabezado para que sea facil de entender
            bw.write("CodigoCurso,CodigoSeccion,CodigoEstudiante,Ponderacion,Nota,FechaRegistro");
            bw.newLine();

            // Escribimos cada nota como una linea del CSV
            for (int i = 0; i < lista.cantidadNotas; i++) {
                Nota n = lista.notas[i];
                bw.write(n.getCodigoCurso() + ","
                        + n.getCodigoSeccion() + ","
                        + n.getCodigoEstudiante() + ","
                        + n.getPonderacion() + ","
                        + n.getNota() + ","
                        + n.getFechaRegistro());
                bw.newLine();
            }

            return "Exportacion exitosa: " + lista.cantidadNotas + " notas guardadas en:\n" + rutaArchivo;

        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }
}
