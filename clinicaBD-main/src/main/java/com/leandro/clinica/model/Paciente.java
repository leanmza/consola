package com.leandro.clinica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paciente_id", unique = true)
    private long id;

    @NotNull
    @Size(max = 20)
    private String nombre;
    @NotNull
    @Size(max = 20)
    private String apellido;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(max = 12)
    private String celular;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> listaTurnos;
}
