package com.gestion.pedidos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public abstract class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean eliminado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Se ejecuta automáticamente antes de persistir por primera vez
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.eliminado = false;
    }
}
