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
// Esta clase implementa Runnabl osea que puede ejecutarse dentro de un hilo
public class HiloSesionesActivas implements Runnable {

    private boolean activo;    //  esto es para saber si el hilo sigue activo o no

    private JTextArea areaConsola;//   texto donde se mostrarán los mensajes
    private AppController controlador;

    //constructor
    public HiloSesionesActivas(JTextArea areaConsola, AppController controlador) {
        this.areaConsola = areaConsola;
        this.controlador = controlador;
        this.activo = true;
    }

// metodo  para detener el hilo
    public void detener() {
        this.activo = false;
    }

    @Override
    public void run() {
        // Define el formato de fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
// Mientras el hilo este  activo, se ejecuta continuamente
        while (activo) {
            try {

                // Obtiene los usuarios están activos desde el controlador
                int cantidad = controlador.getUsuariosActivos();
                // Obtiene la fecha y hora actul
                String timeStamp = sdf.format(new Date());

                //mensaje que se mostrara wn pantalla
                String mensaje = "[Hilo de Sesiones] usuarios Activos: " + cantidad
                        + " - ultima actividad: [" + timeStamp + "]\n";

                SwingUtilities.invokeLater(() -> {
                    areaConsola.append(mensaje);
                    areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
                });
                // El hilo se duerme 10 segundos antes de repetir
                Thread.sleep(10000);

                // por si hay error muestra el error
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null,
                        "Error en el hilo de sesiones activas",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

}
