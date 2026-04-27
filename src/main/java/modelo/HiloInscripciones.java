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
public class HiloInscripciones// Esta clase implementa Runnable, lo que permite ejecutarla en un hilo
        implements Runnable {

    // Variable que controla si el hilo sigue ejecutándose
    private boolean activo;

    // Área de texto donde se mostrarán los mensajes
    private JTextArea areaConsola;

    // Controlador que contiene la lógica del sistema
    private AppController controlador;

    // Constructor
    public HiloInscripciones(JTextArea areaConsola, AppController controlador) {
        this.areaConsola = areaConsola;   // Guarda el área de salida
        this.controlador = controlador;   // Guarda el controlador
        this.activo = true;               // Activa el hilo al iniciar
    }

    //   detener el hilo
    public void detener() {
        this.activo = false;
    }


    @Override
    public void run() {

        // Formato de fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

       
        while (activo) {
            try {

                // Obtiene cuántas inscripciones estan pendientes
                int cantidad = controlador.getInscripcionesPendientes();

                // Obtiene la fecha y hora actual
                String timeStamp = sdf.format(new Date());

                // Crea un mensaje indicando cuantas inscripciones hay
                String mensaje = "[hilo de Inscripciones] Inscripciones Pendientes: "
                        + cantidad + " - Procesando... [" + timeStamp + "]\n";

                // Actualiza la interfaz de forma segura (Swing)
                SwingUtilities.invokeLater(() -> {
                    areaConsola.append(mensaje); // Agrega el mensaje
                    areaConsola.setCaretPosition(areaConsola.getDocument().getLength()); // Scroll automático
                });

                // El hilo espera 8 segundos antes de continuar
                Thread.sleep(8000);

                // Procesa una inscripción la primera en la cola
                Inscripcion procesada = controlador.procesarInscripcion();

                // Si se proceso una inscripcion
                if (procesada != null) {

                    // Crea un mensaje que se mostrara para ver cual se ejecuto 
                    String mensajeProcesada = "[hilo de Inscripciones] Procesada: "
                            + procesada.getNombre() + " (" + procesada.getCodigo() + ") ["
                            + timeStamp + "]\n";

                    // Muestra el mensaje en la interfaz
                    SwingUtilities.invokeLater(() -> {
                        areaConsola.append(mensajeProcesada);
                        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
                    });
                }

            } catch (InterruptedException e) {

                // Si ocurre un error en el hilo, muestra un mensaje
                JOptionPane.showMessageDialog(null,
                        "Error en el hilo de inscripciones",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

                // Detiene el ciclo del hilo
                break;
            }
        }
    }
}
