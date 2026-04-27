/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import controlador.ControladorNotas;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Inscripcion;
import modelo.Nota;
import modelo.Usuario;

/**
 *
 * @author david
 */
public class PanelEstudiante extends javax.swing.JPanel {

    private VentanaPrincipal ventanaPrincipal;

    /**
     * Creates new form PanelEstudiante
     */
    public PanelEstudiante(VentanaPrincipal ventanaPrincipa) {
        // el parametro se llama ventanaPrincipa (sin la l final)
        // si escribieramos ventanaPrincipal estariamos asignando el campo a si mismo
        this.ventanaPrincipal = ventanaPrincipa;
        initComponents();

        // llenamos el combo de genero con las opciones validas
        configurarComboGenero();

        // ponemos los encabezados correctos en las dos tablas
        configurarTablaCursosDisponibles();
        configurarTablaCalificaciones();

        // el codigo no se puede editar, solo se muestra
        txtCodigoPerfil.setEditable(false);

        // conectamos los botones que NetBeans no conecto automaticamente
        btnInscribirse.addActionListener(e -> inscribirse());
        btnDesasignar.addActionListener(e -> desasignarCurso());
        btnExportarHistorial.addActionListener(e -> exportarHistorial());
        btnActualizarPerfil.addActionListener(e -> actualizarPerfil());
    }

    // VentanaPrincipal llama este metodo cada vez que un estudiante inicia sesion
    public void inicializar() {
        llenarTablaCursosDisponibles();
        llenarTablaCalificaciones();
        cargarDatosPerfil();
    }

    // pone Masculino y Femenino en el combo de genero del perfil
    private void configurarComboGenero() {
        cbGeneroPerfil.removeAllItems();
        cbGeneroPerfil.addItem("Masculino");
        cbGeneroPerfil.addItem("Femenino");
    }

    // define las columnas de la tabla de cursos disponibles
    private void configurarTablaCursosDisponibles() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Codigo", "Nombre", "Descripcion", "Creditos", "Seccion", "Instructor", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // el estudiante no puede editar esta tabla
            }
        };
        tablaCursosDisponibles.setModel(modelo);
    }

    // define las columnas de la tabla de calificaciones
    private void configurarTablaCalificaciones() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Curso", "Seccion", "Ponderacion", "Nota", "Fecha", "Promedio", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // el estudiante no puede editar sus notas
            }
        };
        tablaCalificaciones.setModel(modelo);
    }

    // recorre todos los cursos del sistema y los pone en la tabla con el estado del estudiante
    private void llenarTablaCursosDisponibles() {
        DefaultTableModel modelo = (DefaultTableModel) tablaCursosDisponibles.getModel();
        modelo.setRowCount(0); // borramos filas viejas antes de llenar

        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        Curso[] cursos = ventanaPrincipal.getControladorCursos().getCursos();
        int cantidad = ventanaPrincipal.getControladorCursos().getCantidadCursos();

        for (int i = 0; i < cantidad; i++) {
            Curso c = cursos[i];

            // determinamos el estado del estudiante en este curso
            String estado;
            if (usuarioActual != null && ventanaPrincipal.getControlador()
                    .estaInscrito(usuarioActual.getCodigo(), c.getCodigo())) {
                // la inscripcion ya fue procesada y confirmada
                estado = "Inscrito";
            } else if (usuarioActual != null && ventanaPrincipal.getControlador()
                    .estaInscritoPendiente(usuarioActual.getCodigo(), c.getCodigo())) {
                // la inscripcion existe en la cola pero aun no fue procesada
                estado = "Pendiente";
            } else {
                estado = "Disponible";
            }

            modelo.addRow(new Object[]{
                c.getCodigo(),
                c.getNombre(),
                c.getDescripcion(),
                c.getCreditos(),
                c.getSeccion(),
                c.getCodigoInstructor(),
                estado
            });
        }
    }

    // se ejecuta cuando el estudiante presiona el boton Inscribirse
    private void inscribirse() {
        int fila = tablaCursosDisponibles.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un curso de la tabla primero.");
            return;
        }

        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) return;

        String codigoCurso = tablaCursosDisponibles.getValueAt(fila, 0).toString();

        // rechaza si ya esta inscrito o tiene inscripcion pendiente en este mismo curso
        if (ventanaPrincipal.getControlador().estaInscrito(usuarioActual.getCodigo(), codigoCurso)) {
            JOptionPane.showMessageDialog(this, "Ya estas inscrito en este curso.");
            return;
        }
        if (ventanaPrincipal.getControlador().estaInscritoPendiente(usuarioActual.getCodigo(), codigoCurso)) {
            JOptionPane.showMessageDialog(this, "Ya tienes una inscripcion pendiente en este curso.");
            return;
        }

        // rechaza si hay choque de horario: otro curso con la misma seccion
        String seccion = tablaCursosDisponibles.getValueAt(fila, 4).toString();
        if (ventanaPrincipal.getControlador().tieneChoqueSeccion(usuarioActual.getCodigo(), seccion)) {
            JOptionPane.showMessageDialog(this,
                    "No puedes inscribirte: ya tienes un curso en la seccion " + seccion + ".");
            return;
        }

        Inscripcion inscripcion = new Inscripcion(usuarioActual.getCodigo(), usuarioActual.getNombre(), codigoCurso);
        ventanaPrincipal.getControlador().agregarInscripcionPendiente(inscripcion);

        Estudiante[] estudiantes = ventanaPrincipal.getControladorUsuarios().getEstudiantes();
        int cantidad = ventanaPrincipal.getControladorUsuarios().getCantidadEstudiantes();
        for (int i = 0; i < cantidad; i++) {
            if (estudiantes[i].getCodigo().equals(usuarioActual.getCodigo())) {
                estudiantes[i].setCantidadCursosInscritos(estudiantes[i].getCantidadCursosInscritos() + 1);
                break;
            }
        }

        JOptionPane.showMessageDialog(this, "Inscripcion enviada correctamente.");
        llenarTablaCursosDisponibles();
        ventanaPrincipal.getControlador().guardarSistema();
    }

    // se ejecuta cuando el estudiante presiona el boton Desasignar
    private void desasignarCurso() {
        int fila = tablaCursosDisponibles.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un curso de la tabla primero.");
            return;
        }

        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) return;

        String codigoCurso = tablaCursosDisponibles.getValueAt(fila, 0).toString();
        String estado = tablaCursosDisponibles.getValueAt(fila, 6).toString();

        if (!estado.equals("Inscrito") && !estado.equals("Pendiente")) {
            JOptionPane.showMessageDialog(this, "No estas inscrito en este curso.");
            return;
        }

        boolean eliminado = ventanaPrincipal.getControlador()
                .eliminarInscripcion(usuarioActual.getCodigo(), codigoCurso);

        if (eliminado) {
            Estudiante[] estudiantes = ventanaPrincipal.getControladorUsuarios().getEstudiantes();
            int cantidad = ventanaPrincipal.getControladorUsuarios().getCantidadEstudiantes();
            for (int i = 0; i < cantidad; i++) {
                if (estudiantes[i].getCodigo().equals(usuarioActual.getCodigo())) {
                    int cursosActuales = estudiantes[i].getCantidadCursosInscritos();
                    if (cursosActuales > 0) {
                        estudiantes[i].setCantidadCursosInscritos(cursosActuales - 1);
                    }
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, "Desasignado correctamente.");
            llenarTablaCursosDisponibles();
            ventanaPrincipal.getControlador().guardarSistema();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo desasignar el curso.");
        }
    }

    // muestra en la tabla solo las notas que pertenecen al estudiante en sesion
    private void llenarTablaCalificaciones() {
        DefaultTableModel modelo = (DefaultTableModel) tablaCalificaciones.getModel();
        modelo.setRowCount(0); // borramos filas viejas antes de llenar

        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        ControladorNotas controladorNotas = ventanaPrincipal.getControladorNotas();
        Nota[] notas = controladorNotas.getNotas();
        int cantidad = controladorNotas.getCantidadNotas();

        for (int i = 0; i < cantidad; i++) {
            Nota n = notas[i];

            // solo procesamos las notas de este estudiante
            if (n.getCodigoEstudiante().equals(usuarioActual.getCodigo())) {

                // calculamos el promedio ponderado de esta seccion
                double promedio = controladorNotas.calcularPromedio(
                        n.getCodigoEstudiante(), n.getCodigoSeccion());

                String promedioTexto;
                String estado;

                if (promedio < 0) {
                    // calcularPromedio devuelve -1 cuando no hay datos suficientes
                    promedioTexto = "N/A";
                    estado = "N/A";
                } else {
                    promedioTexto = String.format("%.2f", promedio);
                    // 61 o mas es aprobado
                    estado = promedio >= 61 ? "Aprobado" : "Reprobado";
                }

                modelo.addRow(new Object[]{
                    n.getCodigoCurso(),
                    n.getCodigoSeccion(),
                    n.getPonderacion(),
                    n.getNota(),
                    n.getFechaRegistro(),
                    promedioTexto,
                    estado
                });
            }
        }
    }

    // abre un selector de archivo y guarda el historial de notas en CSV
    private void exportarHistorial() {
        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        // el estudiante elige donde guardar el archivo
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar historial CSV");
        // sugerimos un nombre con el codigo del estudiante
        selector.setSelectedFile(new File("historial_" + usuarioActual.getCodigo() + ".csv"));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // el estudiante cancelo
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            // primera linea: encabezados del CSV
            bw.write("CodigoCurso,CodigoSeccion,Ponderacion,Nota,FechaRegistro,Promedio,Estado");
            bw.newLine();

            ControladorNotas controladorNotas = ventanaPrincipal.getControladorNotas();
            Nota[] notas = controladorNotas.getNotas();
            int cantidad = controladorNotas.getCantidadNotas();

            // una linea por cada nota del estudiante
            for (int i = 0; i < cantidad; i++) {
                Nota n = notas[i];
                if (n.getCodigoEstudiante().equals(usuarioActual.getCodigo())) {
                    double promedio = controladorNotas.calcularPromedio(
                            n.getCodigoEstudiante(), n.getCodigoSeccion());
                    String promedioTexto = promedio < 0 ? "N/A" : String.format("%.2f", promedio);
                    String estado = promedio < 0 ? "N/A" : (promedio >= 61 ? "Aprobado" : "Reprobado");

                    bw.write(n.getCodigoCurso() + "," + n.getCodigoSeccion() + ","
                            + n.getPonderacion() + "," + n.getNota() + ","
                            + n.getFechaRegistro() + "," + promedioTexto + "," + estado);
                    bw.newLine();
                }
            }

            JOptionPane.showMessageDialog(this, "Historial exportado correctamente.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
        }
    }

    // pone los datos del estudiante en los campos del formulario de perfil
    private void cargarDatosPerfil() {
        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        // mostramos los datos actuales del estudiante
        txtCodigoPerfil.setText(usuarioActual.getCodigo());
        txtNombrePerfil.setText(usuarioActual.getNombre());
        txtFechaPerfil.setText(usuarioActual.getFechaNacimiento());
        cbGeneroPerfil.setSelectedItem(usuarioActual.getGenero());

        // dejamos vacios los campos de contrasena por seguridad
        txtContrasenaActual1.setText("");
        txtContrasenaNueva.setText("");
    }

    // valida la contrasena actual y guarda los cambios del perfil
    private void actualizarPerfil() {
        Usuario usuarioActual = ventanaPrincipal.getControlador().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        // leemos lo que el estudiante escribio en los campos
        String nombre = txtNombrePerfil.getText().trim();
        String fecha = txtFechaPerfil.getText().trim();
        String genero = cbGeneroPerfil.getSelectedItem().toString();
        String contrasenaActual = txtContrasenaActual1.getText().trim();
        String contrasenaNueva = txtContrasenaNueva.getText().trim();

        // nombre y fecha son obligatorios
        if (nombre.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y la fecha son obligatorios.");
            return;
        }

        // la contrasena actual es obligatoria para poder guardar cualquier cambio
        if (contrasenaActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar tu contrasena actual.");
            return;
        }

        // verificamos que la contrasena actual sea correcta
        if (!contrasenaActual.equals(usuarioActual.getContrasena())) {
            JOptionPane.showMessageDialog(this, "La contrasena actual no es correcta.");
            return;
        }

        // si no escribio nueva contrasena, dejamos la misma de siempre
        String contrasenaFinal = contrasenaNueva.isEmpty() ? contrasenaActual : contrasenaNueva;

        // le pedimos al controlador que guarde los cambios
        boolean actualizado = ventanaPrincipal.getControladorUsuarios().actualizarEstudiante(
                usuarioActual.getCodigo(), nombre, fecha, genero, contrasenaFinal);

        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente.");
            // limpiamos los campos de contrasena por seguridad
            txtContrasenaActual1.setText("");
            txtContrasenaNueva.setText("");
            ventanaPrincipal.getControlador().guardarSistema();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el perfil.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        areaConsola = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        tabsAdministrador = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCursosDisponibles = new javax.swing.JTable();
        btnInscribirse = new javax.swing.JButton();
        btnDesasignar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaCalificaciones = new javax.swing.JTable();
        btnExportarHistorial = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtNombrePerfil = new javax.swing.JTextField();
        txtCodigoPerfil = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtFechaPerfil = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtContrasenaNueva = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        cbGeneroPerfil = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        btnActualizarPerfil = new javax.swing.JButton();
        txtContrasenaActual1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();

        areaConsola.setColumns(20);
        areaConsola.setRows(5);
        jScrollPane1.setViewportView(areaConsola);

        jPanel1.setBackground(new java.awt.Color(102, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("ESTUDIANTE");

        btnCerrar.setText("Cerrar Sesion");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnCerrar))
                .addGap(16, 16, 16))
        );

        jLabel2.setText("Cursos Disponibles");

        tablaCursosDisponibles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaCursosDisponibles);

        btnInscribirse.setText("Inscribirse");

        btnDesasignar.setText("desasignar");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDesasignar)
                            .addComponent(btnInscribirse))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(129, 129, 129))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel2)
                        .addGap(45, 45, 45)
                        .addComponent(btnInscribirse)
                        .addGap(18, 18, 18)
                        .addComponent(btnDesasignar))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        tabsAdministrador.addTab("Cursos disponibles", jPanel2);

        tablaCalificaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaCalificaciones);

        btnExportarHistorial.setText("Exportar calificaciones");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(282, 282, 282)
                        .addComponent(btnExportarHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExportarHistorial)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        tabsAdministrador.addTab("Calificaciones", jPanel3);

        jLabel18.setText("Contrasena");

        txtNombrePerfil.addActionListener(this::txtNombrePerfilActionPerformed);

        jLabel14.setText("Codigo");

        jLabel15.setText("Nombre");

        jLabel16.setText("Fecha");

        cbGeneroPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbGeneroPerfil.addActionListener(this::cbGeneroPerfilActionPerformed);

        jLabel11.setText("Genero");

        btnActualizarPerfil.setText("Actualizar");

        jLabel19.setText("contrasena nueva");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(cbGeneroPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel16))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtFechaPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(txtCodigoPerfil)
                                    .addComponent(txtNombrePerfil))
                                .addGap(80, 80, 80)
                                .addComponent(btnActualizarPerfil))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel19)
                        .addGap(52, 52, 52)
                        .addComponent(txtContrasenaNueva, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(172, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(277, 277, 277)
                    .addComponent(txtContrasenaActual1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(328, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtNombrePerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtFechaPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btnActualizarPerfil)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbGeneroPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtContrasenaNueva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(47, 47, 47))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addContainerGap(219, Short.MAX_VALUE)
                    .addComponent(txtContrasenaActual1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(86, 86, 86)))
        );

        tabsAdministrador.addTab("Perfil", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabsAdministrador, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 726, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsAdministrador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        ventanaPrincipal.cerrarSesion();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtNombrePerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombrePerfilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombrePerfilActionPerformed

    private void cbGeneroPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeneroPerfilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbGeneroPerfilActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaConsola;
    private javax.swing.JButton btnActualizarPerfil;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnDesasignar;
    private javax.swing.JButton btnExportarHistorial;
    private javax.swing.JButton btnInscribirse;
    private javax.swing.JComboBox<String> cbGeneroPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tablaCalificaciones;
    private javax.swing.JTable tablaCursosDisponibles;
    private javax.swing.JTabbedPane tabsAdministrador;
    private javax.swing.JTextField txtCodigoPerfil;
    private javax.swing.JTextField txtContrasenaActual1;
    private javax.swing.JTextField txtContrasenaNueva;
    private javax.swing.JTextField txtFechaPerfil;
    private javax.swing.JTextField txtNombrePerfil;
    // End of variables declaration//GEN-END:variables
}
