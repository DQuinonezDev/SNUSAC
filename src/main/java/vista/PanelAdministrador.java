/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.HiloEstadisticas;
import modelo.HiloInscripciones;
import modelo.HiloSesionesActivas;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Inscripcion;
import modelo.Instructor;
import controlador.ControladorBitacora;
import controlador.ControladorReportes;
import modelo.Bitacora;
import modelo.Listas;

/**
 *
 * @author emily
 */
public class PanelAdministrador extends javax.swing.JPanel {

    private VentanaPrincipal ventanaPrincipal;
    private HiloSesionesActivas tareaSesiones;
    private HiloInscripciones tareaInscripciones;
    private HiloEstadisticas tareaEstadisticas;

    private Thread hiloSesiones;
    private Thread hiloInscripciones;
    private Thread hiloEstadisticas;

    private boolean hilosIniciados;

    /**
     * Creates new form PanelAdministrador
     */
    public PanelAdministrador(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.hilosIniciados = false;
        initComponents();
        configurarComboGenero();
        configurarTablaInstructores();
        llenarTablaInstructores();
        configurarComboGeneroEstudiantes(); // Inicializa el combo de género para estudiantes
        configurarTablaEstudiantes();       // Configura las columnas de la tabla de estudiantes
        llenarTablaEstudiantes();           // Carga los estudiantes existentes en la tabla
        configurarComboInstructores();      // Llena el combo con los instructores registrados
        configurarTablaCursos();            // Configura las columnas de la tabla de cursos
        llenarTablaCursos();                // Carga los cursos existentes en la tabla
        configurarTablaBitacora();
        configurarBotonesBitacora();
        configurarBotonesReportes();
    }

    //iniciamos lo hilos para que se muestren en pantalla
    public void iniciarHilos() {
        if (hilosIniciados) {
            return;
        }
        //ponemos mensaje en el areatezt
        areaConsola.setText("Iniciando Monitoreo de Hilos...\n");

        // mostramos llamando a hilos en modelos
        tareaSesiones = new HiloSesionesActivas(areaConsola, ventanaPrincipal.getControlador());
        hiloSesiones = new Thread(tareaSesiones);
        hiloSesiones.start();
        // mostramos llamando a hilos en modelos

        tareaInscripciones = new HiloInscripciones(areaConsola, ventanaPrincipal.getControlador());
        hiloInscripciones = new Thread(tareaInscripciones);
        hiloInscripciones.start();
        // mostramos llamando a hilos en modelos

        tareaEstadisticas = new HiloEstadisticas(areaConsola, ventanaPrincipal.getControlador());
        hiloEstadisticas = new Thread(tareaEstadisticas);
        hiloEstadisticas.start();

        hilosIniciados = true;
        recargarTablaBitacora();
    }

    private void agregarInscripcion() {
        // este boton era de prueba, la inscripcion real la hace el estudiante desde su panel
        areaConsola.append("Las inscripciones las envian los estudiantes desde su modulo.\n");
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }

    public void detenerHilos() {
        if (!hilosIniciados) {
            return;
        }

        if (tareaSesiones != null) {
            tareaSesiones.detener();
            hiloSesiones.interrupt(); //interrumpe el sleep

        }

        if (tareaInscripciones != null) {
            tareaInscripciones.detener();
            hiloInscripciones.interrupt(); //interrumpe el sleep

        }

        if (tareaEstadisticas != null) {
            tareaEstadisticas.detener();
            hiloEstadisticas.interrupt();
        }

        hilosIniciados = false;
    }

    //llena la tabla de instructores
    private void llenarTablaInstructores() {
        // Obtiene el modelo de la tabla
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaInstructores.getModel();
        modeloTabla.setRowCount(0);// Limpia la tabla
        // Obtiene el arreglo de instructores desde el controlador
        Instructor[] instructores = ventanaPrincipal.getControladorUsuarios().getInstructores();
        int cantidad = ventanaPrincipal.getControladorUsuarios().getCantidadInstructores();
        // Recorre solo los instructores
        for (int i = 0; i < cantidad; i++) {
            Instructor ins = instructores[i]; // Obtiene un instructor

            modeloTabla.addRow(new Object[]{ // Agrega una nueva fila a la tabla con los datos del instructor
                ins.getCodigo(),
                ins.getNombre(),
                ins.getFechaNacimiento(),
                ins.getGenero(),
                ins.getCantidadSeccionesAsignadas()
            });
        }
    }

    private void limpiarCampos() { // esto limpia los campos despues de agregar o haceralguna accion
        txtCodigo1.setText("");
        txtNombre1.setText("");
        txtFechaNacimiento.setText("");
        txtContrasena.setText("");
        cbGenero.setSelectedIndex(0);
    }

    // configura los valores del combo
    private void configurarComboGenero() {
        cbGenero.removeAllItems();
        cbGenero.addItem("Masculino");
        cbGenero.addItem("Femenino");
    }

    // Configura la tabla de instructores con sus columnas y sin celdas editables
    private void configurarTablaInstructores() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Fecha Nac.", "Género", "Secciones"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInstructores.setModel(modeloTabla);
    }

    // Configura los valores del combo de género para el formulario de estudiantes
    private void configurarComboGeneroEstudiantes() {
        cbGeneroEstudiantes.removeAllItems();
        cbGeneroEstudiantes.addItem("Masculino");
        cbGeneroEstudiantes.addItem("Femenino");
    }

    // Configura la tabla de estudiantes con sus columnas y sin celdas editables
    private void configurarTablaEstudiantes() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Fecha Nac.", "Género", "Cursos Inscritos"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEstudiantes.setModel(modeloTabla);
    }

    // Llena la tabla de estudiantes con los datos actuales del controlador
    private void llenarTablaEstudiantes() {
        // Obtiene el modelo de la tabla
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaEstudiantes.getModel();
        modeloTabla.setRowCount(0); // Limpia la tabla antes de recargar

        // Obtiene el arreglo de estudiantes y la cantidad registrada
        Estudiante[] estudiantes = ventanaPrincipal.getControladorUsuarios().getEstudiantes();
        int cantidad = ventanaPrincipal.getControladorUsuarios().getCantidadEstudiantes();

        // Recorre solo los estudiantes registrados
        for (int i = 0; i < cantidad; i++) {
            Estudiante est = estudiantes[i];

            modeloTabla.addRow(new Object[]{ // Agrega una fila por cada estudiante
                est.getCodigo(),
                est.getNombre(),
                est.getFechaNacimiento(),
                est.getGenero(),
                est.getCantidadCursosInscritos()
            });
        }
    }

    // Limpia los campos del formulario de estudiantes después de una operación
    private void limpiarCamposEstudiantes() {
        txtCodigoEstudiantes.setText("");
        txtNombreEstudiantes.setText("");
        txtFechaNacimientoEstudiantes.setText("");
        txtContrasenaEstudiantes.setText("");
        cbGeneroEstudiantes.setSelectedIndex(0);
    }

    // Llena el combo de instructores con todos los instructores registrados
    // Se usa para que el admin pueda elegir quien imparte el curso
    private void configurarComboInstructores() {
        // Limpia el combo antes de llenarlo para no duplicar elementos
        cbInstructorCurso.removeAllItems();

        // Agrega una opcion vacia al inicio para indicar que no hay instructor asignado
        cbInstructorCurso.addItem("Sin instructor");

        // Obtiene el arreglo de instructores del controlador
        modelo.Instructor[] instructores = ventanaPrincipal.getControladorUsuarios().getInstructores();
        int cantidad = ventanaPrincipal.getControladorUsuarios().getCantidadInstructores();

        // Agrega el codigo de cada instructor como una opcion del combo
        for (int i = 0; i < cantidad; i++) {
            cbInstructorCurso.addItem(instructores[i].getCodigo());
        }
    }

    // Configura la tabla de cursos con sus columnas y sin celdas editables
    private void configurarTablaCursos() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Codigo", "Nombre", "Descripcion", "Creditos", "Seccion", "Instructor", "Inscritos"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCurso.setModel(modeloTabla);
    }

    // Llena la tabla de cursos con los datos actuales del controlador
    private void llenarTablaCursos() {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaCurso.getModel();
        modeloTabla.setRowCount(0); // limpia antes de recargar

        Curso[] cursos = ventanaPrincipal.getControladorCursos().getCursos();
        int cantidad = ventanaPrincipal.getControladorCursos().getCantidadCursos();

        // obtenemos las inscripciones confirmadas para poder contar por curso
        Inscripcion[] confirmadas = ventanaPrincipal.getControlador().getInscripcionesConfirmadas();
        int cantConfirmadas = ventanaPrincipal.getControlador().getCantidadInscripcionesConfirmadas();

        for (int i = 0; i < cantidad; i++) {
            Curso cur = cursos[i];

            // contamos cuantos estudiantes confirmados tiene este curso
            int inscritos = 0;
            for (int j = 0; j < cantConfirmadas; j++) {
                if (confirmadas[j].getCodigoCurso().equals(cur.getCodigo())) {
                    inscritos++;
                }
            }

            modeloTabla.addRow(new Object[]{
                cur.getCodigo(),
                cur.getNombre(),
                cur.getDescripcion(),
                cur.getCreditos(),
                cur.getSeccion(),
                cur.getCodigoInstructor(),
                inscritos
            });
        }

        // refresca el combo por si se agregaron nuevos instructores
        configurarComboInstructores();
    }

    // Limpia los campos del formulario de cursos despues de una operacion
    private void limpiarCamposCursos() {
        txtCodigoCursos.setText("");
        txtNombreCursos.setText("");
        txtDescripcion.setText("");
        txtCreditos.setText("");
        txtSeccionCursos.setText("");
        // Regresa el combo al primer elemento que es "Sin instructor"
        cbInstructorCurso.setSelectedIndex(0);
    }

    // Asigna el instructor seleccionado al curso y actualiza los contadores de ambos instructores
    private void asignarInstructor() {
        // leemos el codigo del curso desde el campo de texto
        String codigoCurso = txtCodigoCursos.getText().trim();

        if (codigoCurso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un curso de la tabla primero.");
            return;
        }

        // leemos el instructor nuevo elegido en el combo
        String codigoInstructorNuevo = cbInstructorCurso.getSelectedItem().toString();
        if (codigoInstructorNuevo.equals("Sin instructor")) {
            codigoInstructorNuevo = ""; // cadena vacia significa sin instructor
        }

        // buscamos quien era el instructor ANTERIOR de este curso para bajarle el contador
        Curso[] cursos = ventanaPrincipal.getControladorCursos().getCursos();
        int cantidadCursos = ventanaPrincipal.getControladorCursos().getCantidadCursos();
        String codigoInstructorAnterior = "";

        for (int i = 0; i < cantidadCursos; i++) {
            if (cursos[i].getCodigo().equals(codigoCurso)) {
                codigoInstructorAnterior = cursos[i].getCodigoInstructor();
                break;
            }
        }

        // hacemos la asignacion en el modelo
        boolean asignado = ventanaPrincipal.getControladorCursos().asignarInstructor(codigoCurso, codigoInstructorNuevo);

        if (asignado) {
            registrarBitacora("ASIGNAR_INSTRUCTOR", "EXITO",
                    "Instructor " + codigoInstructorNuevo + " asignado al curso " + codigoCurso);
            Instructor[] instructores = ventanaPrincipal.getControladorUsuarios().getInstructores();
            int cantidadInstructores = ventanaPrincipal.getControladorUsuarios().getCantidadInstructores();

            // si habia un instructor antes, le bajamos 1 a su contador de secciones
            if (!codigoInstructorAnterior.isEmpty()) {
                for (int i = 0; i < cantidadInstructores; i++) {
                    if (instructores[i].getCodigo().equals(codigoInstructorAnterior)) {
                        int actual = instructores[i].getCantidadSeccionesAsignadas();
                        if (actual > 0) {
                            instructores[i].setCantidadSeccionesAsignadas(actual - 1);
                        }
                        break;
                    }
                }
            }

            // si se asigno un instructor nuevo, le subimos 1 a su contador de secciones
            if (!codigoInstructorNuevo.isEmpty()) {
                for (int i = 0; i < cantidadInstructores; i++) {
                    if (instructores[i].getCodigo().equals(codigoInstructorNuevo)) {
                        instructores[i].setCantidadSeccionesAsignadas(
                                instructores[i].getCantidadSeccionesAsignadas() + 1);
                        break;
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Instructor asignado correctamente.");

            // recargamos ambas tablas para mostrar los contadores actualizados
            llenarTablaCursos();
            llenarTablaInstructores();

            limpiarCamposCursos();
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            JOptionPane.showMessageDialog(this, "No se encontro el curso con ese codigo.");
            registrarBitacora("ASIGNAR_INSTRUCTOR", "ERROR",
                    "Curso no encontrado al asignar instructor: " + codigoCurso);
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

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaConsola = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnInfoDesarrollador = new javax.swing.JButton();
        tabsAdministrador = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbGenero = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JTextField();
        txtFechaNacimiento = new javax.swing.JTextField();
        scrollTabla = new javax.swing.JScrollPane();
        tablaInstructores = new javax.swing.JTable();
        txtCodigo1 = new javax.swing.JTextField();
        btnCrear = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNombre1 = new javax.swing.JTextField();
        btnCsvInstructores = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbGeneroEstudiantes = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtContrasenaEstudiantes = new javax.swing.JTextField();
        txtFechaNacimientoEstudiantes = new javax.swing.JTextField();
        scrollTabla1 = new javax.swing.JScrollPane();
        tablaEstudiantes = new javax.swing.JTable();
        txtCodigoEstudiantes = new javax.swing.JTextField();
        btnCrearEstudiantes = new javax.swing.JButton();
        btnActualizarEstudiantes = new javax.swing.JButton();
        btnEliminarEstudiantes = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtNombreEstudiantes = new javax.swing.JTextField();
        btnCsvEstudiantes = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtCodigoCursos = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        btnCrearCurso = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        btnActualizarCurso = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnEliminarCurso = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtNombreCursos = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtCreditos = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        scrollTabla2 = new javax.swing.JScrollPane();
        tablaCurso = new javax.swing.JTable();
        txtSeccionCursos = new javax.swing.JTextField();
        btnAgregarInstructor = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        cbInstructorCurso = new javax.swing.JComboBox<>();
        btnCsvCursos1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        btnMejorDesem = new javax.swing.JButton();
        btnPeorDesem = new javax.swing.JButton();
        btnSeccionesPorRendimiento = new javax.swing.JButton();
        btnHisorial = new javax.swing.JButton();
        btnInscripcionesPorCurso = new javax.swing.JButton();
        btnCalificacionPorSeccion = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        scrollTabla3 = new javax.swing.JScrollPane();
        tablaBitacora = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        txtFiltroTipo = new javax.swing.JTextField();
        txtFiltroOperacion = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        btnFiltrarBitacora = new javax.swing.JButton();
        btnLimpiarBitacora = new javax.swing.JButton();
        btnExportarBitacora = new javax.swing.JButton();

        setBackground(new java.awt.Color(227, 237, 252));

        areaConsola.setColumns(20);
        areaConsola.setRows(5);
        jScrollPane1.setViewportView(areaConsola);

        jPanel1.setBackground(new java.awt.Color(102, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("ADMINISTRADOR");

        btnCerrar.setText("Cerrar Sesion");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        btnInfoDesarrollador.setText("informacion del desarrollador");
        btnInfoDesarrollador.addActionListener(this::btnInfoDesarrolladorActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInfoDesarrollador, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnCerrar)
                    .addComponent(btnInfoDesarrollador))
                .addGap(16, 16, 16))
        );

        jLabel3.setText("Codigo");

        jLabel4.setText("Nombre");

        jLabel5.setText("Fecha Nacimiento");

        cbGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbGenero.addActionListener(this::cbGeneroActionPerformed);

        jLabel6.setText("Genero");

        jLabel7.setText("Contrasena");

        tablaInstructores.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaInstructores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaInstructoresMouseClicked(evt);
            }
        });
        scrollTabla.setViewportView(tablaInstructores);

        btnCrear.setText("Crear");
        btnCrear.addActionListener(this::btnCrearActionPerformed);

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        jLabel2.setText("CREAR INSTRUCTOR");

        txtNombre1.addActionListener(this::txtNombre1ActionPerformed);

        btnCsvInstructores.setText("csv Instructores");
        btnCsvInstructores.addActionListener(this::btnCsvInstructoresActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCodigo1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnCrear)
                                .addGap(18, 18, 18)
                                .addComponent(btnActualizar)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(btnCsvInstructores, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCrear)
                            .addComponent(btnActualizar)
                            .addComponent(btnEliminar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCsvInstructores)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabsAdministrador.addTab("Instructores", jPanel2);

        jLabel8.setText("Codigo");

        jLabel9.setText("Nombre");

        jLabel10.setText("Fecha Nacimiento");

        cbGeneroEstudiantes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbGeneroEstudiantes.addActionListener(this::cbGeneroEstudiantesActionPerformed);

        jLabel11.setText("Genero");

        jLabel12.setText("Contrasena");

        tablaEstudiantes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaEstudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaEstudiantesMouseClicked(evt);
            }
        });
        scrollTabla1.setViewportView(tablaEstudiantes);

        btnCrearEstudiantes.setText("Crear");
        btnCrearEstudiantes.addActionListener(this::btnCrearEstudiantesActionPerformed);

        btnActualizarEstudiantes.setText("Actualizar");
        btnActualizarEstudiantes.addActionListener(this::btnActualizarEstudiantesActionPerformed);

        btnEliminarEstudiantes.setText("Eliminar");
        btnEliminarEstudiantes.addActionListener(this::btnEliminarEstudiantesActionPerformed);

        jLabel13.setText("CREAR ESTUDIANTES");

        txtNombreEstudiantes.addActionListener(this::txtNombreEstudiantesActionPerformed);

        btnCsvEstudiantes.setText("csv Estudiantes");
        btnCsvEstudiantes.addActionListener(this::btnCsvEstudiantesActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbGeneroEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtContrasenaEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFechaNacimientoEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCodigoEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombreEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnCrearEstudiantes)
                                .addGap(18, 18, 18)
                                .addComponent(btnActualizarEstudiantes)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminarEstudiantes))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(btnCsvEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTabla1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigoEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtNombreEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtFechaNacimientoEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbGeneroEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtContrasenaEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCrearEstudiantes)
                            .addComponent(btnActualizarEstudiantes)
                            .addComponent(btnEliminarEstudiantes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCsvEstudiantes)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabsAdministrador.addTab("Estudiantes", jPanel3);

        jLabel14.setText("Codigo");

        btnCrearCurso.setText("Crear");
        btnCrearCurso.addActionListener(this::btnCrearCursoActionPerformed);

        jLabel15.setText("Nombre");

        btnActualizarCurso.setText("Actualizar");
        btnActualizarCurso.addActionListener(this::btnActualizarCursoActionPerformed);

        jLabel16.setText("Descripcion");

        btnEliminarCurso.setText("Eliminar");
        btnEliminarCurso.addActionListener(this::btnEliminarCursoActionPerformed);

        jLabel17.setText("CREAR CURSOS");

        jLabel18.setText("Seccion");

        txtNombreCursos.addActionListener(this::txtNombreCursosActionPerformed);

        jLabel19.setText("Creditos");

        tablaCurso.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaCurso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaCursoMouseClicked(evt);
            }
        });
        scrollTabla2.setViewportView(tablaCurso);

        btnAgregarInstructor.setText("agregar instructor");
        btnAgregarInstructor.addActionListener(this::btnAgregarInstructorActionPerformed);

        jLabel20.setText("Instructor");

        cbInstructorCurso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnCsvCursos1.setText("csv Cursos");
        btnCsvCursos1.addActionListener(this::btnCsvCursos1ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel20)
                                            .addComponent(jLabel19))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCreditos, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(txtDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(txtCodigoCursos, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(txtNombreCursos, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(txtSeccionCursos, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(cbInstructorCurso, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnCrearCurso)
                                .addGap(18, 18, 18)
                                .addComponent(btnActualizarCurso)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminarCurso)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btnCsvCursos1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregarInstructor, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(scrollTabla2, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTabla2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigoCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtNombreCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtSeccionCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(12, 12, 12)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel20)
                                    .addComponent(cbInstructorCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnCrearCurso)
                                    .addComponent(btnActualizarCurso)
                                    .addComponent(btnEliminarCurso)))
                            .addComponent(txtCreditos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAgregarInstructor)
                            .addComponent(btnCsvCursos1))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabsAdministrador.addTab("Cursos", jPanel4);

        btnMejorDesem.setText("Top 5 desempeno");

        btnPeorDesem.setText("top 5 bajo desempeno");

        btnSeccionesPorRendimiento.setText("secciones por Rendimiento");

        btnHisorial.setText("Historial invididual");

        btnInscripcionesPorCurso.setText("Inscripciones por curso");

        btnCalificacionPorSeccion.setText("Calificacion por seccion");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(btnMejorDesem)
                        .addGap(40, 40, 40)
                        .addComponent(btnPeorDesem)
                        .addGap(42, 42, 42)
                        .addComponent(btnSeccionesPorRendimiento))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(btnInscripcionesPorCurso)
                        .addGap(63, 63, 63)
                        .addComponent(btnHisorial))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(404, 404, 404)
                        .addComponent(btnCalificacionPorSeccion)))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMejorDesem)
                    .addComponent(btnPeorDesem)
                    .addComponent(btnSeccionesPorRendimiento))
                .addGap(61, 61, 61)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInscripcionesPorCurso)
                    .addComponent(btnHisorial))
                .addGap(29, 29, 29)
                .addComponent(btnCalificacionPorSeccion)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        tabsAdministrador.addTab("Reportes", jPanel6);

        tablaBitacora.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaBitacora.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaBitacoraMouseClicked(evt);
            }
        });
        scrollTabla3.setViewportView(tablaBitacora);

        jLabel21.setText("Filtrar por usuario");

        jLabel22.setText("Filtrar por operacion");

        btnFiltrarBitacora.setText("Filtrar");

        btnLimpiarBitacora.setText("Limpiar Filtro");

        btnExportarBitacora.setText("exportar ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(scrollTabla3, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltroTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltroOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnFiltrarBitacora)
                            .addComponent(btnLimpiarBitacora))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExportarBitacora)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21)
                                    .addComponent(txtFiltroTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22)
                                    .addComponent(txtFiltroOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnFiltrarBitacora)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnLimpiarBitacora)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(btnExportarBitacora)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(scrollTabla3, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        tabsAdministrador.addTab("Bitacora", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tabsAdministrador, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsAdministrador)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        ventanaPrincipal.cerrarSesion();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombre1ActionPerformed

    private void cbGeneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeneroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbGeneroActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        // Obtiene los datos desde los campos de la interfaz
        String codigo = txtCodigo1.getText().trim();
        String nombre = txtNombre1.getText().trim();             // Nombre
        String fechaNacimiento = txtFechaNacimiento.getText().trim(); // Fecha de nacimiento
        String genero = cbGenero.getSelectedItem().toString();
        String contrasena = txtContrasena.getText().trim();     // Contrasena

// Valida que ningún campo esté vacío
        if (codigo.isEmpty() || nombre.isEmpty() || fechaNacimiento.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios para actualizar.");
            return; // Sale del método si falta informacion
        }

// Llama al controlador para actualizar el instructor
        boolean actualizado = ventanaPrincipal.getControladorUsuarios().actualizarInstructor(
                codigo, nombre, fechaNacimiento, genero, contrasena
        );

// Si se actualizó correctamente
        if (actualizado) {

            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, "Instructor actualizado correctamente.");
            registrarBitacora("ACTUALIZAR_INSTRUCTOR", "EXITO", "Instructor actualizado: " + codigo);

            // Recarga la tabla con los datos actualizados
            llenarTablaInstructores();

            // Limpia los campos del formulario
            limpiarCampos();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {

            // Si no se encontro el instructor
            JOptionPane.showMessageDialog(this, "No se encontró el instructor a actualizar.");
            registrarBitacora("ACTUALIZAR_INSTRUCTOR", "ERROR", "Instructor no encontrado: " + codigo);
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void tablaInstructoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaInstructoresMouseClicked
        int fila = tablaInstructores.getSelectedRow();
        // selecciona al usuario para mostrarlo en la tabla
        if (fila != -1) {
            txtCodigo1.setText(tablaInstructores.getValueAt(fila, 0).toString());
            txtNombre1.setText(tablaInstructores.getValueAt(fila, 1).toString());
            txtFechaNacimiento.setText(tablaInstructores.getValueAt(fila, 2).toString());
            cbGenero.setSelectedItem(tablaInstructores.getValueAt(fila, 3).toString());
        }
    }//GEN-LAST:event_tablaInstructoresMouseClicked

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // Muestra un cuadro de dialogo para pedir el codigo del instructor
        String codigo = JOptionPane.showInputDialog(this, "Ingrese el código del instructor a eliminar:");
        // Valida si el usuario  dejp el campo vacío
        if (codigo == null || codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código.");
            return; // Sale del método
        }
// Llama al controlador para intentar eliminar el instructor
        boolean eliminado = ventanaPrincipal.getControladorUsuarios().eliminarInstructor(codigo.trim());
// Si se elimino correctamente
        if (eliminado) {

            // Muestra mensaje
            JOptionPane.showMessageDialog(this, "Instructor eliminado correctamente.");
            registrarBitacora("ELIMINAR_INSTRUCTOR", "EXITO", "Instructor eliminado: " + codigo.trim());

            // Actualiza la tabla en pantalla
            llenarTablaInstructores();

            // Limpia los campos del formulario
            limpiarCampos();

            // Guarda los cambios en el archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {

            // Si no se encontrp el instructor
            JOptionPane.showMessageDialog(this, "No se encontró un instructor con ese código.");
            registrarBitacora("ELIMINAR_INSTRUCTOR", "ERROR", "Instructor no encontrado: " + codigo.trim());
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    //para crear instructor
    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
        // Obtiene los datos ingresados en los campos de la interfaz
        String codigo = txtCodigo1.getText().trim();
        String nombre = txtNombre1.getText().trim();             // Nombre
        String fechaNacimiento = txtFechaNacimiento.getText().trim(); // Fecha de nacimiento
        String genero = cbGenero.getSelectedItem().toString();
        String contrasena = txtContrasena.getText().trim();     // Contraseña

// Valida que todos los campos estén llenos
        if (codigo.isEmpty() || nombre.isEmpty() || fechaNacimiento.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return; // Detiene el proceso si falta información
        }
// Crea un nuevo objeto Instructor con los datos ingresados
        Instructor instructor = new Instructor(codigo, nombre, fechaNacimiento, genero, contrasena);

// Llama al controlador para intentar agregar el instructor
        boolean agregado = ventanaPrincipal.getControladorUsuarios().agregarInstructor(instructor);

// Si se agregcorrectamente
        if (agregado) {
            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, "Instructor creado correctamente.");
            registrarBitacora("CREAR_INSTRUCTOR", "EXITO", "Instructor creado: " + codigo);
            // Actualiza la tabla para mostrar el nuevo instructor
            llenarTablaInstructores();

            // Limpia los campos del formulario
            limpiarCampos();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {

            JOptionPane.showMessageDialog(this, "No se pudo crear el instructor. Verifica si el código ya existe o si no hay espacio.");
            registrarBitacora("CREAR_INSTRUCTOR", "ERROR", "Fallo al crear instructor: " + codigo);
        }
    }//GEN-LAST:event_btnCrearActionPerformed

    private void cbGeneroEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeneroEstudiantesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbGeneroEstudiantesActionPerformed

    private void tablaEstudiantesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaEstudiantesMouseClicked
        int fila = tablaEstudiantes.getSelectedRow();
        // Carga los datos de la fila seleccionada en los campos del formulario
        if (fila != -1) {
            txtCodigoEstudiantes.setText(tablaEstudiantes.getValueAt(fila, 0).toString());
            txtNombreEstudiantes.setText(tablaEstudiantes.getValueAt(fila, 1).toString());
            txtFechaNacimientoEstudiantes.setText(tablaEstudiantes.getValueAt(fila, 2).toString());
            cbGeneroEstudiantes.setSelectedItem(tablaEstudiantes.getValueAt(fila, 3).toString());
        }
    }//GEN-LAST:event_tablaEstudiantesMouseClicked

    private void btnCrearEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearEstudiantesActionPerformed
        // Obtiene los datos ingresados en los campos del formulario
        String codigo = txtCodigoEstudiantes.getText().trim();
        String nombre = txtNombreEstudiantes.getText().trim();
        String fechaNacimiento = txtFechaNacimientoEstudiantes.getText().trim();
        String genero = cbGeneroEstudiantes.getSelectedItem().toString();
        String contrasena = txtContrasenaEstudiantes.getText().trim();

        // Valida que todos los campos estén llenos antes de continuar
        if (codigo.isEmpty() || nombre.isEmpty() || fechaNacimiento.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return; // Detiene el proceso si falta información
        }

        // Crea un nuevo objeto Estudiante con los datos ingresados
        Estudiante estudiante = new Estudiante(codigo, nombre, fechaNacimiento, genero, contrasena);

        // Llama al controlador para intentar agregar el estudiante
        boolean agregado = ventanaPrincipal.getControladorUsuarios().agregarEstudiante(estudiante);

        // Si se agregó correctamente
        if (agregado) {
            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, "Estudiante creado correctamente.");
            registrarBitacora("CREAR_ESTUDIANTE", "EXITO", "Estudiante creado: " + codigo);

            // Actualiza la tabla para mostrar el nuevo estudiante
            llenarTablaEstudiantes();

            // Limpia los campos del formulario
            limpiarCamposEstudiantes();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear el estudiante. Verifica si el código ya existe o si no hay espacio.");
            registrarBitacora("CREAR_ESTUDIANTE", "ERROR", "Fallo al crear estudiante: " + codigo);
        }
    }//GEN-LAST:event_btnCrearEstudiantesActionPerformed

    private void btnActualizarEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarEstudiantesActionPerformed
        // Obtiene los datos desde los campos de la interfaz
        String codigo = txtCodigoEstudiantes.getText().trim();
        String nombre = txtNombreEstudiantes.getText().trim();
        String fechaNacimiento = txtFechaNacimientoEstudiantes.getText().trim();
        String genero = cbGeneroEstudiantes.getSelectedItem().toString();
        String contrasena = txtContrasenaEstudiantes.getText().trim();

        // Valida que ningún campo esté vacío
        if (codigo.isEmpty() || nombre.isEmpty() || fechaNacimiento.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios para actualizar.");
            return; // Sale del método si falta información
        }

        // Llama al controlador para actualizar el estudiante
        boolean actualizado = ventanaPrincipal.getControladorUsuarios().actualizarEstudiante(
                codigo, nombre, fechaNacimiento, genero, contrasena
        );

        // Si se actualizó correctamente
        if (actualizado) {
            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.");
            registrarBitacora("ACTUALIZAR_ESTUDIANTE", "EXITO", "Estudiante actualizado: " + codigo);

            // Recarga la tabla con los datos actualizados
            llenarTablaEstudiantes();

            // Limpia los campos del formulario
            limpiarCamposEstudiantes();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            // Si no se encontró el estudiante con ese código
            JOptionPane.showMessageDialog(this, "No se encontró el estudiante a actualizar.");
            registrarBitacora("ACTUALIZAR_ESTUDIANTE", "ERROR", "Estudiante no encontrado: " + codigo);
        }
    }//GEN-LAST:event_btnActualizarEstudiantesActionPerformed

    private void btnEliminarEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarEstudiantesActionPerformed
        // Muestra un cuadro de diálogo para pedir el código del estudiante
        String codigo = JOptionPane.showInputDialog(this, "Ingrese el código del estudiante a eliminar:");

        // Valida si el usuario dejó el campo vacío o canceló
        if (codigo == null || codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código.");
            return; // Sale del método
        }

        // Llama al controlador para intentar eliminar el estudiante
        boolean eliminado = ventanaPrincipal.getControladorUsuarios().eliminarEstudiante(codigo.trim());

        // Si se eliminó correctamente
        if (eliminado) {
            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
            registrarBitacora("ELIMINAR_ESTUDIANTE", "EXITO", "Estudiante eliminado: " + codigo.trim());

            // Actualiza la tabla en pantalla
            llenarTablaEstudiantes();

            // Limpia los campos del formulario
            limpiarCamposEstudiantes();

            // Guarda los cambios en el archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            // Si no se encontró el estudiante
            JOptionPane.showMessageDialog(this, "No se encontró un estudiante con ese código.");
            registrarBitacora("ELIMINAR_ESTUDIANTE", "ERROR", "Estudiante no encontrado: " + codigo.trim());
        }
    }//GEN-LAST:event_btnEliminarEstudiantesActionPerformed

    private void txtNombreEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreEstudiantesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreEstudiantesActionPerformed

    private void btnCrearCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearCursoActionPerformed
        // Obtiene los datos ingresados en los campos del formulario
        String codigo = txtCodigoCursos.getText().trim();
        String nombre = txtNombreCursos.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String creditosTexto = txtCreditos.getText().trim();
        String seccion = txtSeccionCursos.getText().trim();

        // Valida que todos los campos estén llenos antes de continuar
        if (codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty()
                || creditosTexto.isEmpty() || seccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        // Valida que créditos sea un número entero positivo
        int creditos;
        try {
            creditos = Integer.parseInt(creditosTexto);
            if (creditos <= 0) {
                JOptionPane.showMessageDialog(this, "Los créditos deben ser un número entero positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo Créditos debe ser un número entero.");
            return;
        }

        // Crea un nuevo objeto Curso con los datos ingresados
        Curso curso = new Curso(codigo, nombre, descripcion, creditos, seccion);

        // Llama al controlador para intentar agregar el curso
        boolean agregado = ventanaPrincipal.getControladorCursos().agregarCurso(curso);

        if (agregado) {
            JOptionPane.showMessageDialog(this, "Curso creado correctamente.");
            registrarBitacora("CREAR_CURSO", "EXITO", "Curso creado: " + codigo);

            // Actualiza la tabla para mostrar el nuevo curso
            llenarTablaCursos();

            // Limpia los campos del formulario
            limpiarCamposCursos();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear el curso. Verifica si el código ya existe o si no hay espacio.");
            registrarBitacora("CREAR_CURSO", "ERROR", "Fallo al crear curso: " + codigo);
        }
    }//GEN-LAST:event_btnCrearCursoActionPerformed

    private void btnActualizarCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarCursoActionPerformed
        // Obtiene los datos desde los campos de la interfaz
        String codigo = txtCodigoCursos.getText().trim();
        String nombre = txtNombreCursos.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String creditosTexto = txtCreditos.getText().trim();
        String seccion = txtSeccionCursos.getText().trim();

        // Valida que ningún campo esté vacío
        if (codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty()
                || creditosTexto.isEmpty() || seccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios para actualizar.");
            return;
        }

        // Valida que créditos sea un número entero positivo
        int creditos;
        try {
            creditos = Integer.parseInt(creditosTexto);
            if (creditos <= 0) {
                JOptionPane.showMessageDialog(this, "Los créditos deben ser un número entero positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo Créditos debe ser un número entero.");
            return;
        }

        // Llama al controlador para actualizar el curso
        boolean actualizado = ventanaPrincipal.getControladorCursos().actualizarCurso(
                codigo, nombre, descripcion, creditos, seccion
        );

        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Curso actualizado correctamente.");
            registrarBitacora("ACTUALIZAR_CURSO", "EXITO", "Curso actualizado: " + codigo);

            // Recarga la tabla con los datos actualizados
            llenarTablaCursos();

            // Limpia los campos del formulario
            limpiarCamposCursos();

            // Guarda los cambios en archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            // Si no se encontró el curso con ese código
            JOptionPane.showMessageDialog(this, "No se encontró el curso a actualizar.");
            registrarBitacora("ACTUALIZAR_CURSO", "ERROR", "Curso no encontrado: " + codigo);
        }
    }//GEN-LAST:event_btnActualizarCursoActionPerformed

    private void btnEliminarCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCursoActionPerformed
        // Muestra un cuadro de diálogo para pedir el código del curso
        String codigo = JOptionPane.showInputDialog(this, "Ingrese el código del curso a eliminar:");

        // Valida si el usuario dejó el campo vacío o canceló
        if (codigo == null || codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código.");
            return;
        }

        // Llama al controlador para intentar eliminar el curso
        boolean eliminado = ventanaPrincipal.getControladorCursos().eliminarCurso(codigo.trim());

        if (eliminado) {
            JOptionPane.showMessageDialog(this, "Curso eliminado correctamente.");
            registrarBitacora("ELIMINAR_CURSO", "EXITO", "Curso eliminado: " + codigo.trim());

            // Actualiza la tabla en pantalla
            llenarTablaCursos();

            // Limpia los campos del formulario
            limpiarCamposCursos();

            // Guarda los cambios en el archivo
            ventanaPrincipal.getControlador().guardarSistema();

        } else {
            // Si no se encontró el curso
            JOptionPane.showMessageDialog(this, "No se encontró un curso con ese código.");
            registrarBitacora("ELIMINAR_CURSO", "ERROR", "Curso no encontrado: " + codigo.trim());
        }
    }//GEN-LAST:event_btnEliminarCursoActionPerformed

    private void txtNombreCursosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreCursosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreCursosActionPerformed

    private void tablaCursoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCursoMouseClicked
        int fila = tablaCurso.getSelectedRow();
        // Carga los datos de la fila seleccionada en los campos del formulario
        if (fila != -1) {
            txtCodigoCursos.setText(tablaCurso.getValueAt(fila, 0).toString());
            txtNombreCursos.setText(tablaCurso.getValueAt(fila, 1).toString());
            txtDescripcion.setText(tablaCurso.getValueAt(fila, 2).toString());
            txtCreditos.setText(tablaCurso.getValueAt(fila, 3).toString());
            txtSeccionCursos.setText(tablaCurso.getValueAt(fila, 4).toString());

            // Obtiene el codigo del instructor guardado en la columna 5
            String codigoInstructor = tablaCurso.getValueAt(fila, 5).toString();

            // Si el curso tiene instructor asignado lo selecciona en el combo
            // Si no tiene, selecciona la opcion "Sin instructor"
            if (codigoInstructor.isEmpty()) {
                cbInstructorCurso.setSelectedIndex(0); // Primer elemento es "Sin instructor"
            } else {
                cbInstructorCurso.setSelectedItem(codigoInstructor);
            }
        }
    }//GEN-LAST:event_tablaCursoMouseClicked

    private void btnCsvEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCsvEstudiantesActionPerformed
        cargarCsvEstudiantes();
    }//GEN-LAST:event_btnCsvEstudiantesActionPerformed

    private void btnAgregarInstructorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarInstructorActionPerformed
        asignarInstructor();
    }//GEN-LAST:event_btnAgregarInstructorActionPerformed

    private void btnCsvInstructoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCsvInstructoresActionPerformed
        cargarCsvInstructores();
    }//GEN-LAST:event_btnCsvInstructoresActionPerformed

    private void btnCsvCursos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCsvCursos1ActionPerformed
        cargarCsvCursos();
    }//GEN-LAST:event_btnCsvCursos1ActionPerformed

    private void tablaBitacoraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBitacoraMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaBitacoraMouseClicked

    private void btnInfoDesarrolladorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoDesarrolladorActionPerformed
        mostrarInfoDesarrollador();
    }//GEN-LAST:event_btnInfoDesarrolladorActionPerformed

    // Abre el selector de archivo, llama al controlador y muestra el reporte de instructores cargados
    private void cargarCsvInstructores() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Seleccionar CSV de Instructores");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos CSV (*.csv)", "csv"));

        if (selector.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return; // Usuario cancelo
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();
        // El controlador muestra el mensaje de exito o error internamente
        ventanaPrincipal.getControladorUsuarios().cargarInstructoresCSV(ruta);

        // Refresca la tabla y persiste los cambios
        llenarTablaInstructores();
        ventanaPrincipal.getControlador().guardarSistema();
    }

    // Abre el selector de archivo, llama al controlador y muestra el reporte de estudiantes cargados
    private void cargarCsvEstudiantes() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Seleccionar CSV de Estudiantes");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos CSV (*.csv)", "csv"));

        if (selector.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return; // Usuario cancelo
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();
        // El controlador muestra el mensaje de exito o error internamente
        ventanaPrincipal.getControladorUsuarios().cargarEstudiantesCSV(ruta);

        // Refresca la tabla y persiste los cambios
        llenarTablaEstudiantes();
        ventanaPrincipal.getControlador().guardarSistema();
    }

    // Abre el selector de archivo, llama al controlador y muestra el reporte de cursos cargados
    private void cargarCsvCursos() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Seleccionar CSV de Cursos");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos CSV (*.csv)", "csv"));

        if (selector.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return; // Usuario cancelo
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();
        // El controlador muestra el mensaje de exito o error internamente
        ventanaPrincipal.getControladorCursos().cargarCursosCSV(ruta);

        // Refresca la tabla y persiste los cambios
        llenarTablaCursos();
        ventanaPrincipal.getControlador().guardarSistema();
    }


    // ─── BITÁCORA ────────────────────────────────────────────────────────────────

    private void configurarTablaBitacora() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Fecha/Hora", "Tipo", "Codigo", "Operacion", "Estado", "Descripcion"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaBitacora.setModel(modelo);
    }

    public void recargarTablaBitacora() {
        Listas lista = ventanaPrincipal.getControlador().getModelo();
        DefaultTableModel modelo = (DefaultTableModel) tablaBitacora.getModel();
        modelo.setRowCount(0);
        for (int i = 0; i < lista.cantidadEventos; i++) {
            if (lista.bitacora[i] == null) continue;
            Bitacora b = lista.bitacora[i];
            modelo.addRow(new Object[]{
                b.getFechaHora(), b.getTipoUsuario(), b.getCodigoUsuario(),
                b.getOperacion(), b.getEstado(), b.getDescripcion()
            });
        }
    }

    private void llenarTablaBitacora(Bitacora[] datos, int cantidad) {
        DefaultTableModel modelo = (DefaultTableModel) tablaBitacora.getModel();
        modelo.setRowCount(0);
        for (int i = 0; i < cantidad; i++) {
            if (datos[i] == null) continue;
            modelo.addRow(new Object[]{
                datos[i].getFechaHora(), datos[i].getTipoUsuario(), datos[i].getCodigoUsuario(),
                datos[i].getOperacion(), datos[i].getEstado(), datos[i].getDescripcion()
            });
        }
    }

    private void filtrarBitacora() {
        String filtroTipo = txtFiltroTipo.getText().trim();
        String filtroOp = txtFiltroOperacion.getText().trim();
        Bitacora[] resultado = ControladorBitacora.filtrar(
                ventanaPrincipal.getControlador().getModelo(), filtroTipo, filtroOp);
        llenarTablaBitacora(resultado, resultado.length);
    }

    private void limpiarFiltroBitacora() {
        txtFiltroTipo.setText("");
        txtFiltroOperacion.setText("");
        recargarTablaBitacora();
    }

    private void exportarBitacora() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Guardar bitacora como CSV");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV (*.csv)", "csv"));
        selector.setSelectedFile(new java.io.File("bitacora.csv"));
        if (selector.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        String ruta = selector.getSelectedFile().getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".csv")) ruta += ".csv";
        String res = ControladorBitacora.exportarCSV(ventanaPrincipal.getControlador().getModelo(), ruta);
        if (res.startsWith("OK:")) {
            JOptionPane.showMessageDialog(this, "Bitacora exportada:\n" + res.substring(3));
        } else {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + res.substring(4));
        }
    }

    private void configurarBotonesBitacora() {
        btnFiltrarBitacora.addActionListener(e -> filtrarBitacora());
        btnLimpiarBitacora.addActionListener(e -> limpiarFiltroBitacora());
        btnExportarBitacora.addActionListener(e -> exportarBitacora());
    }

    // ─── BITÁCORA: registrar evento y recargar tabla ──────────────────────────

    private String codigoAdmin() {
        modelo.Usuario u = ventanaPrincipal.getControlador().getUsuarioActual();
        return u != null ? u.getCodigo() : "admin";
    }

    private void registrarBitacora(String operacion, String estado, String descripcion) {
        ControladorBitacora.registrar(ventanaPrincipal.getControlador().getModelo(),
                "ADMINISTRADOR", codigoAdmin(), operacion, estado, descripcion);
        recargarTablaBitacora();
    }

    // ─── REPORTES ────────────────────────────────────────────────────────────────

    private void configurarBotonesReportes() {
        btnMejorDesem.addActionListener(e -> generarReporteMejorDesempeno());
        btnPeorDesem.addActionListener(e -> generarReportePeorDesempeno());
        btnSeccionesPorRendimiento.addActionListener(e -> generarReporteSeccionesPorRendimiento());
        btnHisorial.addActionListener(e -> generarReporteHistorialIndividual());
        btnInscripcionesPorCurso.addActionListener(e -> generarReporteInscripcionesPorCurso());
        btnCalificacionPorSeccion.addActionListener(e -> generarReporteCalificacionPorSeccion());
    }

    private void mostrarResultadoReporte(String archivo) {
        if (archivo != null) {
            JOptionPane.showMessageDialog(this, "Reporte generado:\n" + archivo);
        } else {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte.");
        }
    }

    private void generarReporteMejorDesempeno() {
        mostrarResultadoReporte(
                ControladorReportes.reporteMejorDesempeno(ventanaPrincipal.getControlador().getModelo()));
    }

    private void generarReportePeorDesempeno() {
        mostrarResultadoReporte(
                ControladorReportes.reportePeorDesempeno(ventanaPrincipal.getControlador().getModelo()));
    }

    private void generarReporteSeccionesPorRendimiento() {
        mostrarResultadoReporte(
                ControladorReportes.reporteSeccionesPorRendimiento(ventanaPrincipal.getControlador().getModelo()));
    }

    private void generarReporteHistorialIndividual() {
        String codigo = JOptionPane.showInputDialog(this, "Ingrese el codigo del estudiante:");
        if (codigo == null || codigo.trim().isEmpty()) return;
        mostrarResultadoReporte(
                ControladorReportes.reporteHistorialIndividual(
                        ventanaPrincipal.getControlador().getModelo(), codigo.trim()));
    }

    private void generarReporteInscripcionesPorCurso() {
        mostrarResultadoReporte(
                ControladorReportes.reporteInscripcionesPorCurso(ventanaPrincipal.getControlador().getModelo()));
    }

    private void generarReporteCalificacionPorSeccion() {
        mostrarResultadoReporte(
                ControladorReportes.reporteCalificacionPorSeccion(ventanaPrincipal.getControlador().getModelo()));
    }

    public void mostrarInfoDesarrollador() {
        JOptionPane.showMessageDialog(this,
                "Desarrollado por:\n"
                + "Emily Sofia Gonzalez Diaz\n"
                + "Carne: 202405086\n\n"
                + "Curso: Introduccion a la Programacion y Computacion 1\n"
                + "Universidad de San Carlos de Guatemala",
                "Informacion del Desarrollador",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaConsola;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnActualizarCurso;
    private javax.swing.JButton btnActualizarEstudiantes;
    private javax.swing.JButton btnAgregarInstructor;
    private javax.swing.JButton btnCalificacionPorSeccion;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnCrearCurso;
    private javax.swing.JButton btnCrearEstudiantes;
    private javax.swing.JButton btnCsvCursos1;
    private javax.swing.JButton btnCsvEstudiantes;
    private javax.swing.JButton btnCsvInstructores;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarCurso;
    private javax.swing.JButton btnEliminarEstudiantes;
    private javax.swing.JButton btnExportarBitacora;
    private javax.swing.JButton btnFiltrarBitacora;
    private javax.swing.JButton btnHisorial;
    private javax.swing.JButton btnInfoDesarrollador;
    private javax.swing.JButton btnInscripcionesPorCurso;
    private javax.swing.JButton btnLimpiarBitacora;
    private javax.swing.JButton btnMejorDesem;
    private javax.swing.JButton btnPeorDesem;
    private javax.swing.JButton btnSeccionesPorRendimiento;
    private javax.swing.JComboBox<String> cbGenero;
    private javax.swing.JComboBox<String> cbGeneroEstudiantes;
    private javax.swing.JComboBox<String> cbInstructorCurso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JScrollPane scrollTabla1;
    private javax.swing.JScrollPane scrollTabla2;
    private javax.swing.JScrollPane scrollTabla3;
    private javax.swing.JTable tablaBitacora;
    private javax.swing.JTable tablaCurso;
    private javax.swing.JTable tablaEstudiantes;
    private javax.swing.JTable tablaInstructores;
    private javax.swing.JTabbedPane tabsAdministrador;
    private javax.swing.JTextField txtCodigo1;
    private javax.swing.JTextField txtCodigoCursos;
    private javax.swing.JTextField txtCodigoEstudiantes;
    private javax.swing.JTextField txtContrasena;
    private javax.swing.JTextField txtContrasenaEstudiantes;
    private javax.swing.JTextField txtCreditos;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtFechaNacimiento;
    private javax.swing.JTextField txtFechaNacimientoEstudiantes;
    private javax.swing.JTextField txtFiltroOperacion;
    private javax.swing.JTextField txtFiltroTipo;
    private javax.swing.JTextField txtNombre1;
    private javax.swing.JTextField txtNombreCursos;
    private javax.swing.JTextField txtNombreEstudiantes;
    private javax.swing.JTextField txtSeccionCursos;
    // End of variables declaration//GEN-END:variables
}
