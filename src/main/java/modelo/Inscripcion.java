/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class Inscripcion implements Serializable {

    // atributos
    private String codigo;       // codigo del estudiante
    private String nombre;       // nombre del estudiante
    private String codigoCurso;  // codigo del curso al que se inscribe

    // constructor de inscripcion
    public Inscripcion(String codigo, String nombre, String codigoCurso) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.codigoCurso = codigoCurso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }
}
