package controlador;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import modelo.Bitacora;
import modelo.Listas;

public class ControladorBitacora {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void registrar(Listas lista, String tipoUsuario, String codigoUsuario,
                                  String operacion, String estado, String descripcion) {
        if (lista.cantidadEventos >= lista.bitacora.length) return;
        String fechaHora = LocalDateTime.now().format(FMT);
        lista.bitacora[lista.cantidadEventos] = new Bitacora(
                fechaHora, tipoUsuario, codigoUsuario, operacion, estado, descripcion);
        lista.cantidadEventos++;
    }

    public static Bitacora[] filtrar(Listas lista, String filtroTipo, String filtroOperacion) {
        Bitacora[] temp = new Bitacora[lista.cantidadEventos];
        int count = 0;
        for (int i = 0; i < lista.cantidadEventos; i++) {
            Bitacora b = lista.bitacora[i];
            boolean matchTipo = filtroTipo.isEmpty()
                    || b.getTipoUsuario().toLowerCase().contains(filtroTipo.toLowerCase())
                    || b.getCodigoUsuario().toLowerCase().contains(filtroTipo.toLowerCase());
            boolean matchOp = filtroOperacion.isEmpty()
                    || b.getOperacion().toLowerCase().contains(filtroOperacion.toLowerCase());
            if (matchTipo && matchOp) {
                temp[count++] = b;
            }
        }
        Bitacora[] resultado = new Bitacora[count];
        for (int i = 0; i < count; i++) resultado[i] = temp[i];
        return resultado;
    }

    public static String exportarCSV(Listas lista, String rutaArchivo) {
        try (FileWriter fw = new FileWriter(rutaArchivo)) {
            fw.write("Fecha/Hora,Tipo Usuario,Codigo Usuario,Operacion,Estado,Descripcion\n");
            for (int i = 0; i < lista.cantidadEventos; i++) {
                Bitacora b = lista.bitacora[i];
                fw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        b.getFechaHora(), b.getTipoUsuario(), b.getCodigoUsuario(),
                        b.getOperacion(), b.getEstado(), b.getDescripcion()));
            }
            return "OK:" + rutaArchivo;
        } catch (IOException e) {
            return "ERR:" + e.getMessage();
        }
    }
}
