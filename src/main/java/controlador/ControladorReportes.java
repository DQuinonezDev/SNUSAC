package controlador;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import modelo.Curso;
import modelo.Listas;
import modelo.Nota;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ControladorReportes {

    private static final DateTimeFormatter FMT_NOMBRE = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
    private static final DateTimeFormatter FMT_TITULO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final float MARGEN = 50f;
    private static final int MAX_LINEAS = 3000;

    private static String nombreArchivo(String tipo) {
        return LocalDateTime.now().format(FMT_NOMBRE) + "_" + tipo + ".pdf";
    }

    // Reemplaza caracteres con tilde para evitar errores de codificacion en PDFBox
    private static String limpiar(String texto) {
        if (texto == null) return "";
        return texto
            .replace('á', 'a').replace('é', 'e').replace('í', 'i').replace('ó', 'o').replace('ú', 'u')
            .replace('Á', 'A').replace('É', 'E').replace('Í', 'I').replace('Ó', 'O').replace('Ú', 'U')
            .replace('ñ', 'n').replace('Ñ', 'N').replace('ü', 'u').replace('Ü', 'U')
            .replaceAll("[^\\x20-\\x7E]", "?");
    }

    // Escribe un arreglo de lineas en un PDF.
    // Las lineas que empiezan con "##" se renderizan en negrita como encabezados.
    private static String generarPDF(String[] lineas, int cantLineas, String tipo) {
        String nombre = nombreArchivo(tipo);
        PDDocument doc = new PDDocument();
        try {
            PDPage pagina = new PDPage(PDRectangle.A4);
            doc.addPage(pagina);
            float altoPagina = pagina.getMediaBox().getHeight();
            float y = altoPagina - MARGEN;
            PDPageContentStream cs = new PDPageContentStream(doc, pagina);

            for (int i = 0; i < cantLineas; i++) {
                if (y < MARGEN + 15) {
                    cs.close();
                    pagina = new PDPage(PDRectangle.A4);
                    doc.addPage(pagina);
                    cs = new PDPageContentStream(doc, pagina);
                    y = altoPagina - MARGEN;
                }
                String linea = lineas[i];
                boolean esEncabezado = linea.startsWith("##");
                String texto = limpiar(esEncabezado ? linea.substring(2) : linea);
                float tamano = esEncabezado ? 13f : 10f;

                cs.beginText();
                cs.setFont(esEncabezado ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, tamano);
                cs.newLineAtOffset(MARGEN, y);
                cs.showText(texto);
                cs.endText();
                y -= (tamano + 5);
            }

            cs.close();
            doc.save(nombre);
            doc.close();
            return nombre;
        } catch (IOException e) {
            try { doc.close(); } catch (IOException ex) { /* ignorar */ }
            return null;
        }
    }

    private static int cabecera(String[] lineas, int idx, String titulo) {
        lineas[idx++] = "##Sancarlista Academy";
        lineas[idx++] = "##" + titulo;
        lineas[idx++] = "Generado: " + LocalDateTime.now().format(FMT_TITULO);
        lineas[idx++] = "------------------------------------------------------------";
        lineas[idx++] = "";
        return idx;
    }

    private static String buscarNombre(Listas lista, String codigo) {
        for (int i = 0; i < lista.cantidadEstudiantes; i++) {
            if (lista.estudiantes[i].getCodigo().equals(codigo)) {
                return lista.estudiantes[i].getNombre();
            }
        }
        return codigo;
    }

    // Acumula suma y conteo de notas por clave (estudiante o seccion).
    // Devuelve cuantas claves unicas se encontraron.
    private static int acumularPorClave(Nota[] notas, int cantNotas, boolean porEstudiante,
                                         String[] claves, double[] sumas, int[] counts) {
        int num = 0;
        for (int i = 0; i < cantNotas; i++) {
            Nota n = notas[i];
            String clave = porEstudiante ? n.getCodigoEstudiante() : n.getCodigoSeccion();
            int pos = -1;
            for (int j = 0; j < num; j++) {
                if (claves[j].equals(clave)) { pos = j; break; }
            }
            if (pos == -1 && num < claves.length) {
                claves[num] = clave;
                pos = num++;
            }
            if (pos >= 0) {
                sumas[pos] += n.getNota();
                counts[pos]++;
            }
        }
        return num;
    }

    private static void ordenarDesc(String[] claves, double[] promedios, int num) {
        for (int i = 0; i < num - 1; i++) {
            for (int j = 0; j < num - i - 1; j++) {
                if (promedios[j] < promedios[j + 1]) {
                    double tmpD = promedios[j]; promedios[j] = promedios[j + 1]; promedios[j + 1] = tmpD;
                    String tmpS = claves[j]; claves[j] = claves[j + 1]; claves[j + 1] = tmpS;
                }
            }
        }
    }

    private static void ordenarAsc(String[] claves, double[] promedios, int num) {
        for (int i = 0; i < num - 1; i++) {
            for (int j = 0; j < num - i - 1; j++) {
                if (promedios[j] > promedios[j + 1]) {
                    double tmpD = promedios[j]; promedios[j] = promedios[j + 1]; promedios[j + 1] = tmpD;
                    String tmpS = claves[j]; claves[j] = claves[j + 1]; claves[j + 1] = tmpS;
                }
            }
        }
    }

    // 1. Top 5 estudiantes con mejor promedio
    public static String reporteMejorDesempeno(Listas lista) {
        String[] lineas = new String[MAX_LINEAS];
        int idx = cabecera(lineas, 0, "Top 5 - Mejor Desempeno Academico");

        String[] codigos = new String[lista.cantidadEstudiantes + 1];
        double[] sumas = new double[lista.cantidadEstudiantes + 1];
        int[] counts = new int[lista.cantidadEstudiantes + 1];
        int num = acumularPorClave(lista.notas, lista.cantidadNotas, true, codigos, sumas, counts);

        double[] promedios = new double[num];
        for (int i = 0; i < num; i++) promedios[i] = counts[i] > 0 ? sumas[i] / counts[i] : 0;
        ordenarDesc(codigos, promedios, num);

        lineas[idx++] = "##Pos  Estudiante (Codigo)                  Promedio";
        int top = Math.min(5, num);
        for (int i = 0; i < top; i++) {
            String nombre = buscarNombre(lista, codigos[i]);
            lineas[idx++] = String.format("%d     %-36s %.2f", i + 1,
                    nombre + " (" + codigos[i] + ")", promedios[i]);
        }
        if (num == 0) lineas[idx++] = "No hay notas registradas.";

        return generarPDF(lineas, idx, "MejorDesempeno");
    }

    // 2. Top 5 estudiantes con peor promedio
    public static String reportePeorDesempeno(Listas lista) {
        String[] lineas = new String[MAX_LINEAS];
        int idx = cabecera(lineas, 0, "Top 5 - Bajo Desempeno Academico");

        String[] codigos = new String[lista.cantidadEstudiantes + 1];
        double[] sumas = new double[lista.cantidadEstudiantes + 1];
        int[] counts = new int[lista.cantidadEstudiantes + 1];
        int num = acumularPorClave(lista.notas, lista.cantidadNotas, true, codigos, sumas, counts);

        double[] promedios = new double[num];
        for (int i = 0; i < num; i++) promedios[i] = counts[i] > 0 ? sumas[i] / counts[i] : 0;
        ordenarAsc(codigos, promedios, num);

        lineas[idx++] = "##Pos  Estudiante (Codigo)                  Promedio";
        int top = Math.min(5, num);
        for (int i = 0; i < top; i++) {
            String nombre = buscarNombre(lista, codigos[i]);
            lineas[idx++] = String.format("%d     %-36s %.2f", i + 1,
                    nombre + " (" + codigos[i] + ")", promedios[i]);
        }
        if (num == 0) lineas[idx++] = "No hay notas registradas.";

        return generarPDF(lineas, idx, "PeorDesempeno");
    }

    // 3. Secciones ordenadas por promedio de notas (descendente)
    public static String reporteSeccionesPorRendimiento(Listas lista) {
        String[] lineas = new String[MAX_LINEAS];
        int idx = cabecera(lineas, 0, "Secciones por Rendimiento");

        String[] secciones = new String[lista.cantidadCursos + 1];
        double[] sumas = new double[lista.cantidadCursos + 1];
        int[] counts = new int[lista.cantidadCursos + 1];
        int num = acumularPorClave(lista.notas, lista.cantidadNotas, false, secciones, sumas, counts);

        double[] promedios = new double[num];
        for (int i = 0; i < num; i++) promedios[i] = counts[i] > 0 ? sumas[i] / counts[i] : 0;
        ordenarDesc(secciones, promedios, num);

        lineas[idx++] = "##Seccion               Promedio    Notas Registradas";
        for (int i = 0; i < num; i++) {
            lineas[idx++] = String.format("%-22s  %.2f        %d", secciones[i], promedios[i], counts[i]);
        }
        if (num == 0) lineas[idx++] = "No hay notas registradas.";

        return generarPDF(lineas, idx, "SeccionesPorRendimiento");
    }

    // 4. Historial individual de un estudiante
    public static String reporteHistorialIndividual(Listas lista, String codigoEstudiante) {
        String[] lineas = new String[MAX_LINEAS];
        String nombre = buscarNombre(lista, codigoEstudiante);
        int idx = cabecera(lineas, 0, "Historial Individual - " + nombre);
        lineas[idx++] = "Estudiante: " + nombre + " (Codigo: " + codigoEstudiante + ")";
        lineas[idx++] = "";
        lineas[idx++] = "##Curso           Seccion    Nota    Ponderacion  Fecha";

        int cantNotas = 0;
        double sumaNotas = 0;
        for (int i = 0; i < lista.cantidadNotas; i++) {
            Nota n = lista.notas[i];
            if (n.getCodigoEstudiante().equals(codigoEstudiante)) {
                lineas[idx++] = String.format("%-16s %-10s %-7.2f %-12.2f %s",
                        n.getCodigoCurso(), n.getCodigoSeccion(),
                        n.getNota(), n.getPonderacion(), n.getFechaRegistro());
                sumaNotas += n.getNota();
                cantNotas++;
            }
        }

        if (cantNotas == 0) {
            lineas[idx++] = "No hay notas registradas para este estudiante.";
        } else {
            lineas[idx++] = "";
            lineas[idx++] = String.format("##Promedio general: %.2f  (%d notas)", sumaNotas / cantNotas, cantNotas);
        }

        return generarPDF(lineas, idx, "HistorialIndividual");
    }

    // 5. Cantidad de inscripciones confirmadas por curso
    public static String reporteInscripcionesPorCurso(Listas lista) {
        String[] lineas = new String[MAX_LINEAS];
        int idx = cabecera(lineas, 0, "Inscripciones por Curso");
        lineas[idx++] = "##Codigo                  Nombre                   Inscritos";

        int totalInscritos = 0;
        for (int i = 0; i < lista.cantidadCursos; i++) {
            Curso c = lista.cursos[i];
            int inscritos = 0;
            for (int j = 0; j < lista.cantidadInscripcionesConfirmadas; j++) {
                if (lista.inscripcionesConfirmadas[j].getCodigoCurso().equals(c.getCodigo())) {
                    inscritos++;
                }
            }
            lineas[idx++] = String.format("%-24s %-24s %d", c.getCodigo(), c.getNombre(), inscritos);
            totalInscritos += inscritos;
        }

        if (lista.cantidadCursos == 0) lineas[idx++] = "No hay cursos registrados.";
        lineas[idx++] = "";
        lineas[idx++] = "##Total inscripciones confirmadas: " + totalInscritos;

        return generarPDF(lineas, idx, "InscripcionesPorCurso");
    }

    // 6. Promedio de calificaciones por seccion con detalle de estudiantes
    public static String reporteCalificacionPorSeccion(Listas lista) {
        String[] lineas = new String[MAX_LINEAS];
        int idx = cabecera(lineas, 0, "Calificacion Promedio por Seccion");

        // Obtener secciones unicas
        String[] secciones = new String[lista.cantidadCursos + 200];
        int numSec = 0;
        for (int i = 0; i < lista.cantidadNotas; i++) {
            String sec = lista.notas[i].getCodigoSeccion();
            boolean existe = false;
            for (int j = 0; j < numSec; j++) {
                if (secciones[j].equals(sec)) { existe = true; break; }
            }
            if (!existe && numSec < secciones.length) secciones[numSec++] = sec;
        }

        for (int s = 0; s < numSec; s++) {
            String sec = secciones[s];
            lineas[idx++] = "##Seccion: " + sec;
            lineas[idx++] = "  Estudiante (Codigo)            Promedio";

            String[] estCods = new String[lista.cantidadEstudiantes + 1];
            double[] estSumas = new double[lista.cantidadEstudiantes + 1];
            int[] estCounts = new int[lista.cantidadEstudiantes + 1];
            int numEst = 0;

            for (int i = 0; i < lista.cantidadNotas; i++) {
                Nota n = lista.notas[i];
                if (!n.getCodigoSeccion().equals(sec)) continue;
                String cod = n.getCodigoEstudiante();
                int pos = -1;
                for (int j = 0; j < numEst; j++) {
                    if (estCods[j].equals(cod)) { pos = j; break; }
                }
                if (pos == -1 && numEst < estCods.length) { estCods[numEst] = cod; pos = numEst++; }
                if (pos >= 0) { estSumas[pos] += n.getNota(); estCounts[pos]++; }
            }

            double sumaSec = 0;
            for (int i = 0; i < numEst; i++) {
                double prom = estCounts[i] > 0 ? estSumas[i] / estCounts[i] : 0;
                String nombre = buscarNombre(lista, estCods[i]);
                lineas[idx++] = String.format("  %-32s %.2f", nombre + " (" + estCods[i] + ")", prom);
                sumaSec += prom;
            }
            if (numEst > 0) {
                lineas[idx++] = String.format("  ##Promedio de seccion: %.2f", sumaSec / numEst);
            } else {
                lineas[idx++] = "  Sin notas registradas.";
            }
            lineas[idx++] = "";
        }

        if (numSec == 0) lineas[idx++] = "No hay notas registradas.";

        return generarPDF(lineas, idx, "CalificacionPorSeccion");
    }
}
