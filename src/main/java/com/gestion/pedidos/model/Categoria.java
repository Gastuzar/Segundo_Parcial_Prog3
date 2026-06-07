package com.gestion.pedidos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true, exclude = "productos")
@Builder
public class Categoria extends Base {

    private String nombre;
    private String descripcion;

    // Relación OneToMany unidireccional con Producto
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "categoria_id")
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();
}