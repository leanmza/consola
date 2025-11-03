package com.leandro.clinica;

import com.leandro.clinica.DTO.DoctorDTO;
import com.leandro.clinica.DTO.PacienteDTO;
import com.leandro.clinica.DTO.TurnoDTO;
import com.leandro.clinica.model.Doctor;
import com.leandro.clinica.model.Especialidad;
import com.leandro.clinica.model.Horarios;
import com.leandro.clinica.model.Paciente;
import com.leandro.clinica.model.Turno;
import com.leandro.clinica.service.IDoctorService;
import com.leandro.clinica.service.IPacienteService;
import com.leandro.clinica.service.ITurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ClinicaApplication implements CommandLineRunner {
    // Arranque de la app de consola: acá vivo el menú principal y los submenús.
    // La idea es que el usuario pueda operar todo desde la consola sin tocar los endpoints.

    @Autowired
    private IPacienteService pacienteService;

    @Autowired
    private IDoctorService doctorService;

    @Autowired
    private ITurnoService turnoService;

    public static void main(String[] args) {
        // Punto de entrada clásico de Spring Boot. Levanta el contexto y después corre el CommandLineRunner.
        SpringApplication.run(ClinicaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Apenas inicia la app, muestro el menú principal y dejo al usuario navegar por submenús.
        Scanner sc = new Scanner(System.in);
        boolean continuar = true;
        while (continuar) {
            limpiarConsola();
            System.out.println("+--------------------------------------------------+");
            System.out.println("|                 CLINICA - MENU                   |");
            System.out.println("+--------------------------------------------------+");
            System.out.println("| 1) Pacientes                                     |");
            System.out.println("| 2) Doctores                                      |");
            System.out.println("| 3) Turnos                                        |");
            System.out.println("|                                                  |");
            System.out.println("| 0) Salir                                         |");
            System.out.println("+--------------------------------------------------+");
            System.out.print("Seleccione una opción: ");
            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1 -> menuPacientes(sc);   // Todo lo relacionado a pacientes
                case 2 -> menuDoctores(sc);    // Todo lo relacionado a doctores
                case 3 -> menuTurnos(sc);      // Todo lo relacionado a turnos
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida");
            }
            if (continuar) {
                System.out.println();
                pause(sc);
            }
            System.out.println();
        }
    }

    private void menuPacientes(Scanner sc) {
        // Submenú de Pacientes: listar, buscar, crear, actualizar, eliminar.
        boolean volver = false;
        while (!volver) {
            limpiarConsola();
            System.out.println("+-------------------- PACIENTES --------------------+");
            System.out.println("| 1) Listar                                         |");
            System.out.println("| 2) Buscar por ID                                  |");
            System.out.println("| 3) Crear                                          |");
            System.out.println("| 4) Actualizar                                     |");
            System.out.println("| 5) Eliminar                                       |");
            System.out.println("|                                                   |");
            System.out.println("| 0) Volver                                         |");
            System.out.println("+--------------------------------------------------+");
            System.out.print("Seleccione una opción: ");
            int op = leerEntero(sc);
            switch (op) {
                case 1 -> listarPacientes();
                case 2 -> buscarPacientePorId(sc);
                case 3 -> crearPaciente(sc);
                case 4 -> actualizarPaciente(sc);
                case 5 -> eliminarPaciente(sc);
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida");
            }
            if (!volver) {
                System.out.println();
                pause(sc);
            }
        }
    }

    private void menuDoctores(Scanner sc) {
        // Submenú de Doctores: mismas operaciones CRUD que pacientes.
        boolean volver = false;
        while (!volver) {
            limpiarConsola();
            System.out.println("+--------------------- DOCTORES ---------------------+");
            System.out.println("| 1) Listar                                         |");
            System.out.println("| 2) Buscar por ID                                  |");
            System.out.println("| 3) Crear                                          |");
            System.out.println("| 4) Actualizar                                     |");
            System.out.println("| 5) Eliminar                                       |");
            System.out.println("|                                                   |");
            System.out.println("| 0) Volver                                         |");
            System.out.println("+--------------------------------------------------+");
            System.out.print("Seleccione una opción: ");
            int op = leerEntero(sc);
            switch (op) {
                case 1 -> listarDoctores();
                case 2 -> buscarDoctorPorId(sc);
                case 3 -> crearDoctor(sc);
                case 4 -> actualizarDoctor(sc);
                case 5 -> eliminarDoctor(sc);
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida");
            }
            if (!volver) {
                System.out.println();
                System.out.println("Presione Enter para continuar...");
                sc.nextLine();
            }
        }
    }

    private void menuTurnos(Scanner sc) {
        // Submenú de Turnos: consultas y operaciones de asignación, reserva y cancelación.
        boolean volver = false;
        while (!volver) {
            limpiarConsola();
            System.out.println("+---------------------- TURNOS ----------------------+");
            System.out.println("| 1) Listar todos                                   |");
            System.out.println("| 2) Buscar por ID                                  |");
            System.out.println("| 3) Listar pendientes                              |");
            System.out.println("| 4) Listar cancelados                              |");
            System.out.println("| 5) Buscar por doctor (nombre y apellido)          |");
            System.out.println("| 6) Buscar por paciente (nombre y apellido)        |");
            System.out.println("| 7) Asignar automático                             |");
            System.out.println("| 8) Reservar por fecha y hora                      |");
            System.out.println("| 9) Cancelar por ID                                |");
            System.out.println("|                                                   |");
            System.out.println("| 0) Volver                                         |");
            System.out.println("+--------------------------------------------------+");
            System.out.print("Seleccione una opción: ");
            int op = leerEntero(sc);
            switch (op) {
                case 1 -> listarTurnos();
                case 2 -> buscarTurnoPorId(sc);
                case 3 -> listarTurnosPendientes();
                case 4 -> listarTurnosCancelados();
                case 5 -> buscarTurnosPorDoctor(sc);
                case 6 -> buscarTurnosPorPaciente(sc);
                case 7 -> asignarTurno(sc);
                case 8 -> reservarTurno(sc);
                case 9 -> cancelarTurno(sc);
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida");
            }
            if (!volver) {
                System.out.println();
                System.out.println("Presione Enter para continuar...");
                sc.nextLine();
            }
        }
    }

    private int leerEntero(Scanner sc) {
        // Lee un entero del Scanner y consume el salto de línea restante.
        while (!sc.hasNextInt()) {
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private long leerLong(Scanner sc) {
        // Igual que leerEntero pero para long.
        while (!sc.hasNextLong()) {
            sc.next();
        }
        long val = sc.nextLong();
        sc.nextLine();
        return val;
    }

    private String leerLinea(Scanner sc, String prompt) {
        // Muestra un prompt y devuelve la línea completa ingresada.
        System.out.print(prompt);
        return sc.nextLine();
    }

    private LocalTime leerHora(Scanner sc, String prompt) {
        // Espera una hora en formato HH:mm y la parsea a LocalTime.
        System.out.print(prompt);
        String t = sc.nextLine();
        return LocalTime.parse(t);
    }

    private LocalDate leerFecha(Scanner sc, String prompt) {
        // Espera una fecha en formato YYYY-MM-DD y la parsea a LocalDate.
        System.out.print(prompt);
        String d = sc.nextLine();
        return LocalDate.parse(d);
    }

    private void listarPacientes() {
        // Pide al servicio la lista de pacientes y la imprime de forma amigable.
        List<PacienteDTO> lista = pacienteService.getPacientes();
        if (lista.size() == 1 && lista.get(0).getMensajeError() != null) {
            System.out.println("- " + lista.get(0).getMensajeError());
            return;
        }
        System.out.println("-- Pacientes --");
        for (PacienteDTO p : lista) {
            printPaciente(p);
        }
    }

    private void buscarPacientePorId(Scanner sc) {
        // Busca un paciente puntual por ID y muestra el resultado.
        System.out.print("ID paciente: ");
        long id = leerLong(sc);
        PacienteDTO p = pacienteService.getPacienteById(id);
        if (p.getMensajeError() != null) {
            System.out.println("- " + p.getMensajeError());
        } else {
            printPaciente(p);
        }
    }

    private void crearPaciente(Scanner sc) {
        // Carga rápida de datos mínimos para dar de alta un paciente.
        Paciente p = inputPaciente(sc);
        pacienteService.createPaciente(p);
        System.out.println("OK");
    }

    private void actualizarPaciente(Scanner sc) {
        // Pide el ID y los nuevos datos, y delega al servicio la actualización.
        System.out.print("ID paciente: ");
        long id = leerLong(sc);
        Paciente p = inputPaciente(sc);
        pacienteService.updatePaciente(id, p);
        System.out.println("OK");
    }

    private void eliminarPaciente(Scanner sc) {
        // Elimina un paciente por ID.
        System.out.print("ID paciente: ");
        long id = leerLong(sc);
        pacienteService.deletePaciente(id);
        System.out.println("OK");
    }

    private void listarDoctores() {
        // Listado de doctores con datos básicos y su horario.
        List<DoctorDTO> lista = doctorService.getDoctores();
        if (lista.size() == 1 && lista.get(0).getMensajeError() != null) {
            System.out.println("- " + lista.get(0).getMensajeError());
            return;
        }
        System.out.println("-- Doctores --");
        for (DoctorDTO d : lista) {
            printDoctor(d);
        }
    }

    private void buscarDoctorPorId(Scanner sc) {
        // Busca un doctor puntual por ID.
        System.out.print("ID doctor: ");
        long id = leerLong(sc);
        DoctorDTO d = doctorService.getDoctorById(id);
        if (d.getMensajeError() != null) {
            System.out.println("- " + d.getMensajeError());
        } else {
            printDoctor(d);
        }
    }

    private void crearDoctor(Scanner sc) {
        // Alta de doctor pidiendo especialidad y horario de atención.
        Doctor d = inputDoctor(sc);
        doctorService.createDoctor(d);
        System.out.println("OK");
    }

    private void actualizarDoctor(Scanner sc) {
        // Actualiza datos del doctor manteniendo el ID.
        System.out.print("ID doctor: ");
        long id = leerLong(sc);
        Doctor d = inputDoctor(sc);
        doctorService.updateDoctor(id, d);
        System.out.println("OK");
    }

    private void eliminarDoctor(Scanner sc) {
        // Elimina un doctor por ID.
        System.out.print("ID doctor: ");
        long id = leerLong(sc);
        doctorService.deleteDoctor(id);
        System.out.println("OK");
    }

    private void listarTurnos() {
        // Lista todos los turnos ordenados por fecha.
        List<TurnoDTO> lista = turnoService.getTurnos();
        printTurnosLista("-- Turnos --", lista);
    }

    private void buscarTurnoPorId(Scanner sc) {
        // Busca un turno puntual por ID.
        System.out.print("ID turno: ");
        long id = leerLong(sc);
        TurnoDTO t = turnoService.getTurnoById(id);
        printTurno(t);
    }

    private void listarTurnosPendientes() {
        // Muestra solo los turnos con fecha/hora desde ahora en adelante.
        List<TurnoDTO> lista = turnoService.getTurnosPendientes();
        printTurnosLista("-- Turnos pendientes --", lista);
    }

    private void listarTurnosCancelados() {
        // Muestra los turnos que fueron cancelados (ocupado = false a futuro).
        List<TurnoDTO> lista = turnoService.getTurnosCancelados();
        printTurnosLista("-- Turnos cancelados --", lista);
    }

    private void buscarTurnosPorDoctor(Scanner sc) {
        // Filtra turnos por nombre y apellido del doctor.
        String nombre = leerLinea(sc, "Nombre doctor: ");
        String apellido = leerLinea(sc, "Apellido doctor: ");
        List<TurnoDTO> lista = turnoService.getTurnosByNombreDoctor(nombre, apellido);
        printTurnosLista("-- Turnos del doctor " + nombre + " " + apellido + " --", lista);
    }

    private void buscarTurnosPorPaciente(Scanner sc) {
        // Filtra turnos por nombre y apellido del paciente.
        String nombre = leerLinea(sc, "Nombre paciente: ");
        String apellido = leerLinea(sc, "Apellido paciente: ");
        List<TurnoDTO> lista = turnoService.getTurnosByNombrePaciente(nombre, apellido);
        printTurnosLista("-- Turnos del paciente " + nombre + " " + apellido + " --", lista);
    }

    private void asignarTurno(Scanner sc) {
        // Asigna automáticamente el próximo turno disponible para el doctor elegido.
        long idPaciente;
        long idDoctor;
        System.out.print("ID paciente: ");
        idPaciente = leerLong(sc);
        System.out.print("ID doctor: ");
        idDoctor = leerLong(sc);
        Paciente p = new Paciente();
        p.setId(idPaciente);
        Doctor d = new Doctor();
        d.setId(idDoctor);
        Turno turno = new Turno();
        turno.setPaciente(p);
        turno.setDoctor(d);
        TurnoDTO asignado = turnoService.asignarTurno(turno);
        printTurno(asignado);
    }

    private void reservarTurno(Scanner sc) {
        // Reserva un turno en una fecha y hora específicas si está dentro del horario del doctor y disponible.
        long idPaciente;
        long idDoctor;
        LocalDate fecha = leerFecha(sc, "Fecha (YYYY-MM-DD): ");
        LocalTime hora = leerHora(sc, "Hora (HH:mm): ");
        System.out.print("ID paciente: ");
        idPaciente = leerLong(sc);
        System.out.print("ID doctor: ");
        idDoctor = leerLong(sc);
        Paciente p = new Paciente();
        p.setId(idPaciente);
        Doctor d = new Doctor();
        d.setId(idDoctor);
        Turno turno = new Turno();
        turno.setPaciente(p);
        turno.setDoctor(d);
        turno.setFechaHora(LocalDateTime.of(fecha, hora));
        TurnoDTO reservado = turnoService.reservarTurno(turno);
        printTurno(reservado);
    }

    private void cancelarTurno(Scanner sc) {
        // Cancela un turno por ID (marca ocupado=false en la base).
        System.out.print("ID turno: ");
        long id = leerLong(sc);
        turnoService.deleteTurno(id);
        System.out.println("OK");
    }

    private void limpiarConsola() {
        // Intento limpiar la consola con secuencias ANSI y como refuerzo agrego líneas en blanco.
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {}
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    private void pause(Scanner sc) {
        System.out.println("Presione Enter para continuar...");
        sc.nextLine();
    }

    private Paciente inputPaciente(Scanner sc) {
        String nombre = leerLinea(sc, "Nombre: ");
        String apellido = leerLinea(sc, "Apellido: ");
        String email = leerLinea(sc, "Email: ");
        String celular = leerLinea(sc, "Celular: ");
        Paciente p = new Paciente();
        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setEmail(email);
        p.setCelular(celular);
        return p;
    }

    private Doctor inputDoctor(Scanner sc) {
        String nombre = leerLinea(sc, "Nombre: ");
        String apellido = leerLinea(sc, "Apellido: ");
        String email = leerLinea(sc, "Email: ");
        String celular = leerLinea(sc, "Celular: ");
        String especialidadNombre = leerLinea(sc, "Especialidad: ");
        LocalTime horaInicio = leerHora(sc, "Hora inicio (HH:mm): ");
        LocalTime horaFin = leerHora(sc, "Hora fin (HH:mm): ");
        Doctor d = new Doctor();
        d.setNombre(nombre);
        d.setApellido(apellido);
        d.setEmail(email);
        d.setCelular(celular);
        Especialidad e = new Especialidad();
        e.setNombre(especialidadNombre);
        d.setEspecialidad(e);
        Horarios h = new Horarios();
        h.setHoraInicio(horaInicio);
        h.setHoraFin(horaFin);
        d.setHorarios(h);
        return d;
    }

    private void printPaciente(PacienteDTO p) {
        // Salida ordenada y legible de un paciente.
        System.out.println("# " + p.getId() + " - " + p.getNombre() + " " + p.getApellido());
        System.out.println("  Email: " + p.getEmail());
        System.out.println("  Celular: " + p.getCelular());
        System.out.println();
    }

    private void printDoctor(DoctorDTO d) {
        // Salida ordenada y legible de un doctor.
        System.out.println("# " + d.getId() + " - " + d.getNombre() + " " + d.getApellido());
        System.out.println("  Especialidad: " + d.getEspecialidad());
        System.out.println("  Email: " + d.getEmail());
        System.out.println("  Celular: " + d.getCelular());
        System.out.println("  Horario: " + d.getHoraInicio() + " - " + d.getHoraFin());
        System.out.println();
    }

    private void printTurnosLista(String titulo, List<TurnoDTO> lista) {
        // Recorre una lista de turnos y los imprime con un título. Si viene un error, lo muestra.
        if (lista.size() == 1 && lista.get(0).getMensajeError() != null) {
            System.out.println("- " + lista.get(0).getMensajeError());
            return;
        }
        System.out.println(titulo);
        for (TurnoDTO t : lista) {
            printTurno(t);
        }
    }

    private void printTurno(TurnoDTO t) {
        // Impresión detallada de un turno, mostrando paciente, doctor y estado.
        if (t == null) {
            System.out.println("- No encontrado");
            return;
        }
        if (t.getMensajeError() != null) {
            System.out.println("- " + t.getMensajeError());
            return;
        }
        String pac = t.getPaciente() != null ? (t.getPaciente().getNombre() + " " + t.getPaciente().getApellido()) : "";
        String doc = t.getDoctor() != null ? (t.getDoctor().getNombre() + " " + t.getDoctor().getApellido()) : "";
        System.out.println("# Turno " + t.getId());
        System.out.println("  Fecha: " + t.getFecha() + "  Hora: " + t.getHora());
        System.out.println("  Paciente: " + pac);
        System.out.println("  Doctor: " + doc);
        System.out.println("  Estado: " + (t.isOcupado() ? "Ocupado" : "Disponible"));
        System.out.println();
    }
}
