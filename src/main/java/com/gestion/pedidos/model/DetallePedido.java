package com.gestion.pedidos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalles_pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class DetallePedido extends Base {
    private int cantidad;
    private Double subtotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @ToString.Exclude
    private Pedido pedido;
}
