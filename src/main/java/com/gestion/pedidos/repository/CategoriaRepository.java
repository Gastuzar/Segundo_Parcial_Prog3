package com.gestion.pedidos.repository;

import com.gestion.pedidos.model.Categoria;

public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    // Hereda todo el CRUD de BaseRepository<Categoria>
    // No requiere métodos adicionales
}