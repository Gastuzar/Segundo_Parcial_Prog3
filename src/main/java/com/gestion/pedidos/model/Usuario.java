package com.gestion.pedidos.model;

import com.gestion.pedidos.enums.Rol;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class Usuario extends Base {
    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String mail;

    private String celular;
    private String contraseña;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Pedido> pedidos = new ArrayList<>();
}
