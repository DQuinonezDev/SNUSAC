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
public class Nota implements Serializable {

    //atributos
    private String codigoCurso;
    private String codigoSeccion;
    private String codigoEstudiante;
    private double ponderacion;
    private double nota;
    private String fechaRegistro;

    //constructor de nota, que se usa al crear un curso
    public Nota(String codigoCurso, String codigoSeccion, String codigoEstudiante, double ponderacion, double nota, String fechaRegistro) {
        this.codigoCurso = codigoCurso;
        this.codigoSeccion = codigoSeccion;
        this.codigoEstudiante = codigoEstudiante;
        this.ponderacion = ponderacion;
        this.nota = nota;
        this.fechaRegistro = fechaRegistro;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getCodigoSeccion() {
        return codigoSeccion;
    }

    public void setCodigoSeccion(String codigoSeccion) {
        this.codigoSeccion = codigoSeccion;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
  
}
