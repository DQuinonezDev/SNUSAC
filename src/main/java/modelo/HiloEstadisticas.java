/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.AppController;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author david
 */
//el hilo se encarga de mostrar estadísticas del sistema
public class HiloEstadisticas implements Runnable {

    private boolean activo; // Controla si el hilo 
    private JTextArea areaConsola;
    private AppController controlador;

    // Constructor
    public HiloEstadisticas(JTextArea areaConsola, AppController controlador) {
        this.areaConsola = areaConsola;
        this.controlador = controlador;
        this.activo = true;
    }

    //detener el hilo
    public void detener() {
        this.activo = false;
    }

    @Override
    public void run() {
        // Formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        while (activo) {
            try {

                // Obtiene estadísticas desde el controlador
                int cursos = controlador.getCantidadCursosActivos();
                int estudiantes = controlador.getCantidadEstudiantesRegistrados();
                int notas = controlador.getCantidadCalificacionesRegistradas();
                // Obtiene la fecha actual
                String timeStamp = sdf.format(new Date());

                String mensaje = "[hilo de Estadisticas] Cursos Activos: " + cursos
                        + " | Estudiantes Registrados: " + estudiantes
                        + " | Calificaciones Registradas: " + notas
                        + " | [" + timeStamp + "]\n";

                SwingUtilities.invokeLater(() -> {
                    areaConsola.append(mensaje);
                    areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
                });
                // Espera 15 segundos antes de repetir
                Thread.sleep(15000);

            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null,
                        "Error en el hilo de estadisticas",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

}
