package com.leandro.clinica;

import com.leandro.clinica.DTO.DoctorDTO;
import com.leandro.clinica.DTO.PacienteDTO;
import com.leandro.clinica.DTO.TurnoDTO;
import com.leandro.clinica.model.Doctor;
import com.leandro.clinica.model.Especialidad;
import com.leandro.clinica.model.Horarios;
import com.leandro.clinica.model.Paciente;
import com.leandro.clinica.model.Turno;
import com.leandro.clinica.service.*;
import jakarta.validation.ConstraintViolationException;
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

    @Autowired
    private IEspecialidadService especialidadService;

    @Autowired
    private IHorariosService horariosService;

    private Scanner sc;

    public static void main(String[] args) {
        // Punto de entrada clásico de Spring Boot. Levanta el contexto y después corre el CommandLineRunner.
        SpringApplication.run(ClinicaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Apenas inicia la app, muestro el menú principal y dejo al usuario navegar por submenús.
        sc = new Scanner(System.in);
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
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuPacientes();   // Todo lo relacionado a pacientes
                case 2 -> menuDoctores();    // Todo lo relacionado a doctores
                case 3 -> menuTurnos();      // Todo lo relacionado a turnos
                case 0 -> {
                    continuar = false;
                    System.exit(0);
                }
                default -> System.out.println("Opcion invalida");
            }
            if (continuar) {
                System.out.println();
                pause();
            }
            System.out.println();
        }
    }

    private void menuPacientes() {
        // Submenú de Pacientes: listar, buscar, crear, actualizar, eliminar.
        boolean volver = false;
        while (!volver) {
            limpiarConsola();
            System.out.println("+-------------------- PACIENTES --------------------+");
            System.out.println("| 1) Listar                                         |");
            System.out.println("| 2) Buscar por ID                                 |");
            System.out.println("| 3) Crear                                          |");
            System.out.println("| 4) Actualizar                                     |");
            System.out.println("| 5) Eliminar                                       |");
            System.out.println("|                                                   |");
            System.out.println("| 0) Volver                                         |");
            System.out.println("+--------------------------------------------------+");
            System.out.print("Seleccione una opción: ");
            int op = leerEntero();
            switch (op) {
                case 1 -> listarPacientes();
                case 2 -> buscarPacientePorId();
                case 3 -> crearPaciente();
                case 4 -> actualizarPaciente();
                case 5 -> eliminarPaciente();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida");
            }
            if (!volver) {
                System.out.println();
                pause();
            }
        }
    }

    private void menuDoctores() {
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
            int op = leerEntero();
            switch (op) {
                case 1 -> listarDoctores();
                case 2 -> buscarDoctorPorId();
                case 3 -> crearDoctor();
                case 4 -> actualizarDoctor();
                case 5 -> eliminarDoctor();
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

    private void menuTurnos() {
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
            int op = leerEntero();
            switch (op) {
                case 1 -> listarTurnos();
                case 2 -> buscarTurnoPorId();
                case 3 -> listarTurnosPendientes();
                case 4 -> listarTurnosCancelados();
                case 5 -> buscarTurnosPorDoctor();
                case 6 -> buscarTurnosPorPaciente();
                case 7 -> asignarTurno();
                case 8 -> reservarTurno();
                case 9 -> cancelarTurno();
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

    private int leerEntero() {
        // Lee un entero del Scanner y consume el salto de línea restante.
        while (!sc.hasNextInt()) {
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private long leerLong() {
        // Igual que leerEntero pero para long.
        while (!sc.hasNextLong()) {
            sc.next();
        }
        long val = sc.nextLong();
        sc.nextLine();
        return val;
    }

    private String leerLinea(String prompt) {
        // Muestra un prompt y devuelve la línea completa ingresada.
        System.out.print(prompt);
        return sc.nextLine();
    }

    private String validarCampo(String mensaje) {
        while (true) {
            try {
                if (mensaje.contains("Email")) {
                    return validarEmail(leerLinea(mensaje));
                }
                return validarInput(leerLinea(mensaje));
            } catch (NullPointerException e) {
                System.out.println(e.getMessage() + ". Intente nuevamente.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Intente nuevamente.");
            }
        }
    }

    private LocalTime leerHora(String prompt) {
        // Espera una hora en formato HH:mm y la parsea a LocalTime.
        System.out.print(prompt);
        String hora = sc.nextLine();
        return LocalTime.parse(hora);
    }

    private LocalDate leerFecha(String prompt) {
        // Espera una fecha en formato YYYY-MM-DD y la parsea a LocalDate.
        System.out.print(prompt);
        String fecha = sc.nextLine();
        return LocalDate.parse(fecha);
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

    private void buscarPacientePorId() {
        // Busca un paciente puntual por ID y muestra el resultado.
        System.out.print("ID paciente: ");
        long id = leerLong();
        PacienteDTO p = pacienteService.getPacienteById(id);
        if (p.getMensajeError() != null) {
            System.out.println("- " + p.getMensajeError());
        } else {
            printPaciente(p);
        }
    }

    private void crearPaciente() {
        // Carga rápida de datos mínimos para dar de alta un paciente.
        Paciente p = inputPaciente();
        pacienteService.createPaciente(p);
        System.out.println("Paciente creado exitosamente");
    }

    private void actualizarPaciente() {
        // Pide el ID y los nuevos datos, y delega al servicio la actualización.

        System.out.print("ID paciente: ");
        long id = leerLong();
        PacienteDTO pacienteDto = pacienteService.getPacienteById(id);
        printPaciente(pacienteDto);
        Paciente pacienteEditar = menuEditarPaciente(pacienteDto);
        pacienteService.updatePaciente(id, pacienteEditar);
        System.out.println("Paciente actualizado exitosamente");

    }

    private Paciente menuEditarPaciente(PacienteDTO paciente) {
        int opcion;

        Paciente pacienteEditar = new Paciente();
        pacienteEditar.setNombre(paciente.getNombre());
        pacienteEditar.setApellido(paciente.getApellido());
        pacienteEditar.setEmail(paciente.getEmail());
        pacienteEditar.setCelular(paciente.getCelular());

        System.out.println("Seleccione el campo a actualizar");
        System.out.println("""
                1. Nombre
                2. Apellido
                3. Email
                4. Celular
                
                0. Cancelar """);
        opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion) {
            case 1:
                pacienteEditar.setNombre(validarCampo("Actualizar nombre: "));
                break;
            case 2:
                pacienteEditar.setApellido(validarCampo("Actualizar apellido: "));
                break;
            case 3:
                pacienteEditar.setEmail(validarCampo("Actualizar email: "));
                break;
            case 4:
                pacienteEditar.setCelular(validarCampo("Actualizar celular: "));
                break;
            case 0: break;
            default:
                System.out.println("Opción no válida");
        }
        return pacienteEditar;
    }

    private void eliminarPaciente() {
        // Elimina un paciente por ID.
        System.out.print("ID paciente: ");
        long id = leerLong();
        pacienteService.deletePaciente(id);
        System.out.println("Paciente eliminado exitosamente");
    }

    private void listarDoctores() {
        // Listado de doctores con datos básicos y su horario.
        List<DoctorDTO> lista = doctorService.getDoctores();
        if (lista.size() == 1 && lista.get(0).getMensajeError() != null) {
            System.out.println("- " + lista.get(0).getMensajeError());
            return;
        }
        System.out.println("-- Doctores --");
        for (DoctorDTO doctor : lista) {
            printDoctor(doctor);
        }
    }

    private void buscarDoctorPorId() {
        // Busca un doctor puntual por ID.
        System.out.print("ID doctor: ");
        long id = leerLong();
        DoctorDTO doctorDto = doctorService.getDoctorById(id);
        if (doctorDto.getMensajeError() != null) {
            System.out.println("- " + doctorDto.getMensajeError());
        } else {
            printDoctor(doctorDto);
        }
    }

    private void crearDoctor() {
        Doctor doctor = inputDoctor();
        try {
            doctorService.createDoctor(doctor);
            System.out.println("Doctor creado exitosamente");
        } catch (ConstraintViolationException e) {
            System.out.println("❌ Error de validación:");
            e.getConstraintViolations()
                    .forEach(v -> System.out.println("- " + v.getPropertyPath() + ": " + v.getMessage()));
        }
    }

    private void actualizarDoctor() {
        // Actualiza datos del doctor manteniendo el ID.
        System.out.print("ID doctor: ");
        long id = leerLong();
        DoctorDTO doctorDTO = doctorService.getDoctorById(id);
        printDoctor(doctorDTO);
        Doctor doctorEditar = menuEditarDoctor(doctorDTO);
        doctorService.updateDoctor(id, doctorEditar);
        System.out.println("Doctor actualizado exitosamente");
    }

    private Doctor menuEditarDoctor(DoctorDTO doctorDTO) {
        int opcion;

        Doctor doctorEditar = new Doctor();
        doctorEditar.setNombre(doctorDTO.getNombre());
        doctorEditar.setApellido(doctorDTO.getApellido());
        doctorEditar.setEmail(doctorDTO.getEmail());
        doctorEditar.setCelular(doctorDTO.getCelular());
        doctorEditar.setEspecialidad(especialidadService.getEspecialidadByName(doctorDTO.getEspecialidad()));
        doctorEditar.setHorarios(horariosService.getHorariosPorRango(LocalTime.parse(doctorDTO.getHoraInicio()), LocalTime.parse(doctorDTO.getHoraFin())));

        System.out.println("Seleccione el campo a actualizar");
        System.out.println("""
                1. Nombre
                2. Apellido
                3. Email
                4. Celular
                5. Especialidad
                6. Horario
                
                0. Cancelar""");
        opcion = sc.nextInt();
        sc.nextLine();
        switch (opcion) {
            case 1:
                doctorEditar.setNombre(validarCampo("Actualizar nombre: "));
                break;
            case 2:
                doctorEditar.setApellido(validarCampo("Actualizar apellido: "));
                break;
            case 3:
                doctorEditar.setEmail(validarCampo("Actualizar email: "));
                break;
            case 4:
                doctorEditar.setCelular(validarCampo("Actualizar celular: "));
                break;
            case 5: doctorEditar.setEspecialidad(especialidadService.createEspecialidad(validarCampo("Actualizar especialidad: ")));
            break;
            case 6:
                Horarios horarios = new Horarios();
                horarios.setHoraInicio(leerHora("Actualizar hora inicio: "));
                horarios.setHoraFin(leerHora("Actualizar hora fin: "));
                doctorEditar.setHorarios(horariosService.createHorario(horarios));
                break;
            case 0: break;

            default:
                System.out.println("Opción no válida");

        }
        return doctorEditar;
    }

    private void eliminarDoctor() {
        // Elimina un doctor por ID.
        System.out.print("ID doctor: ");
        long id = leerLong();
        doctorService.deleteDoctor(id);
        System.out.println("Doctor eliminado exitosamente");
    }

    private void listarTurnos() {
        // Lista todos los turnos ordenados por fecha.
        List<TurnoDTO> lista = turnoService.getTurnos();
        printTurnosLista("-- Turnos --", lista);
    }

    private void buscarTurnoPorId() {
        // Busca un turno puntual por ID.
        System.out.print("ID turno: ");
        long id = leerLong();
        TurnoDTO turnoDto = turnoService.getTurnoById(id);
        if(turnoDto.getMensajeError() != null ){
            System.out.println("- " + turnoDto.getMensajeError());
        } else {
            printTurno(turnoDto);
        }
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

    private void buscarTurnosPorDoctor() {
        // Filtra turnos por nombre y apellido del doctor.
        String nombre = leerLinea("Nombre doctor: ");
        String apellido = leerLinea("Apellido doctor: ");
        List<TurnoDTO> lista = turnoService.getTurnosByNombreDoctor(nombre, apellido);
        printTurnosLista("-- Turnos del doctor " + nombre + " " + apellido + " --", lista);
    }

    private void buscarTurnosPorPaciente() {
        // Filtra turnos por nombre y apellido del paciente.
        String nombre = leerLinea("Nombre paciente: ");
        String apellido = leerLinea("Apellido paciente: ");
        List<TurnoDTO> lista = turnoService.getTurnosByNombrePaciente(nombre, apellido);
        printTurnosLista("-- Turnos del paciente " + nombre + " " + apellido + " --", lista);
    }

    private void asignarTurno() {
        // Asigna automáticamente el próximo turno disponible para el doctor elegido.
        long idPaciente;
        long idDoctor;
        System.out.print("ID paciente: ");
        idPaciente = leerLong();
        System.out.print("ID doctor: ");
        idDoctor = leerLong();
        Paciente paciente = new Paciente();
        paciente.setId(idPaciente);
        Doctor doctor = new Doctor();
        doctor.setId(idDoctor);
        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setDoctor(doctor);
        TurnoDTO asignado = turnoService.asignarTurno(turno);
        printTurno(asignado);
    }

    private void reservarTurno() {
        // Reserva un turno en una fecha y hora específicas si está dentro del horario del doctor y disponible.
        long idPaciente;
        long idDoctor;
        LocalDate fecha = leerFecha("Fecha (YYYY-MM-DD): ");
        LocalTime hora = leerHora("Hora (HH:mm): ");
        System.out.print("ID paciente: ");
        idPaciente = leerLong();
        System.out.print("ID doctor: ");
        idDoctor = leerLong();
        Paciente paciente = new Paciente();
        paciente.setId(idPaciente);
        Doctor doctor = new Doctor();
        doctor.setId(idDoctor);
        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setDoctor(doctor);
        turno.setFechaHora(LocalDateTime.of(fecha, hora));
        TurnoDTO reservado = turnoService.reservarTurno(turno);
        printTurno(reservado);
    }

    private void cancelarTurno() {
        // Cancela un turno por ID (marca ocupado=false en la base).
        System.out.print("ID turno: ");
        long id = leerLong();
        turnoService.deleteTurno(id);
        System.out.println("Turno cancelado exitosamente");
    }

    private void limpiarConsola() {
        // Intento limpiar la consola con secuencias ANSI y como refuerzo agrego líneas en blanco.
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {
        }
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    private void pause() {
        System.out.println("Presione Enter para continuar...");
        sc.nextLine();
    }

    private Paciente inputPaciente() {
        Paciente paciente = new Paciente();
        paciente.setNombre(validarCampo("Nombre: "));
        paciente.setApellido(validarCampo("Apellido: "));
        paciente.setEmail(validarCampo("Email: "));
        paciente.setCelular(validarCampo("Celular: "));

        return paciente;
    }

    private String validarInput(String input) {
        if (input == null || input.isBlank()) {
            throw new NullPointerException("Campos obligatorio");
        }
        return input;
    }

    private String validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email == null || email.isBlank() || !email.matches(regex)) {
            throw new IllegalArgumentException("Email inválido");
        }
        return email;
    }

    private Doctor inputDoctor() {
        Doctor doctor = new Doctor();
        Especialidad especialidad = new Especialidad();
        Horarios horario = new Horarios();

        doctor.setNombre(validarCampo("Nombre: "));
        doctor.setApellido(validarCampo("Apellido: "));
        doctor.setEmail(validarCampo("Email: "));
        doctor.setCelular(validarCampo("Celular: "));

        especialidad.setNombre(validarCampo("Especialidad: "));
        doctor.setEspecialidad(especialidad);
        horario.setHoraInicio(leerHora("Hora inicio (HH:mm): "));
        horario.setHoraFin(leerHora("Hora fin (HH:mm): "));

        doctor.setHorarios(horario);

        return doctor;
    }

    private void printPaciente(PacienteDTO pacienteDto) {
        // Salida ordenada y legible de un paciente.
        System.out.println("# " + pacienteDto.getId() + " - " + pacienteDto.getNombre() + " " + pacienteDto.getApellido());
        System.out.println("  Email: " + pacienteDto.getEmail());
        System.out.println("  Celular: " + pacienteDto.getCelular());
        System.out.println();
    }

    private void printDoctor(DoctorDTO doctorDto) {
        // Salida ordenada y legible de un doctor.
        System.out.println("# " + doctorDto.getId() + " - " + doctorDto.getNombre() + " " + doctorDto.getApellido());
        System.out.println("  Especialidad: " + doctorDto.getEspecialidad());
        System.out.println("  Email: " + doctorDto.getEmail());
        System.out.println("  Celular: " + doctorDto.getCelular());
        System.out.println("  Horario: " + doctorDto.getHoraInicio() + " - " + doctorDto.getHoraFin());
        System.out.println();
    }

    private void printTurnosLista(String titulo, List<TurnoDTO> lista) {
        // Recorre una lista de turnos y los imprime con un título. Si viene un error, lo muestra.
        if (lista.size() == 1 && lista.get(0).getMensajeError() != null) {
            System.out.println("- " + lista.get(0).getMensajeError());
            return;
        }
        System.out.println(titulo);
        for (TurnoDTO turno : lista) {
            printTurno(turno);
        }
    }

    private void printTurno(TurnoDTO turno) {
        // Impresión detallada de un turno, mostrando paciente, doctor y estado.
        if (turno == null) {
            System.out.println("- No encontrado");
            return;
        }
        if (turno.getMensajeError() != null) {
            System.out.println("- " + turno.getMensajeError());
            return;
        }

        String pac = turno.getPaciente() != null ? (turno.getPaciente().getNombre() + " " + turno.getPaciente().getApellido()) : "";
        String doc = turno.getDoctor() != null ? (turno.getDoctor().getNombre() + " " + turno.getDoctor().getApellido()) : "";
        System.out.println("# Turno " + turno.getId());
        System.out.println("  Fecha: " + turno.getFecha() + "  Hora: " + turno.getHora());
        System.out.println("  Paciente: " + pac);
        System.out.println("  Doctor: " + doc);
        System.out.println("  Estado: " + (turno.isOcupado() ? "Ocupado" : "Disponible"));
        System.out.println();
    }
}
