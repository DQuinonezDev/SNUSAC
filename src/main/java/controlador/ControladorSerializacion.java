/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import modelo.Listas;

/**
 *
 * @author david
 */

//esta clase se encarga de GUARDAR y CARGAR los datos del sistema en archivos
public class ControladorSerializacion {
    
    //metodo para guardar las listas 
     public static void guardarListas(Listas lista) {
        try {
            // Crear archivo de salida (datos.ser)
            FileOutputStream archivo = new FileOutputStream("datos.ser");

            // Permite guardar objetos completos
            ObjectOutputStream salida = new ObjectOutputStream(archivo);

            // Guarda todo el objeto modelo en el archivo
            salida.writeObject(lista);

            // Cerrar flujos
            salida.close();
            archivo.close();

            System.out.println("Datos guardados correctamente en datos.ser");

        } catch (Exception e) {
            // Si ocurre error
            System.out.println("Error al guardar datos: " + e.getMessage());
        }
    }

    /**
     * Método para cargar el modelo desde un archivo
     */
    public static Listas cargarListas() {
        try {
            // Abrir archivo existente
            FileInputStream archivo = new FileInputStream("datos.ser");

            //  leer objetos completos
            ObjectInputStream entrada = new ObjectInputStream(archivo);

            // llee el objeto y convierte a lista
            Listas list = (Listas) entrada.readObject();

            // Cerrar flujos
            entrada.close();
            archivo.close();

            System.out.println("Datos cargados correctamente desde datos.ser");

            return list;

        } catch (Exception e) {
            // Si el archivo no existe o hay error
            System.out.println("No se encontro archivo de datos. Se crearan listas nuevas.");

            // Retorna una lista nueva vacia
            return new Listas();
        }
    }
}
