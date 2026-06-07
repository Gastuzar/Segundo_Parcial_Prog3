package com.gestion.pedidos.repository;

import com.gestion.pedidos.model.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class ProductoRepository extends BaseRepository<Producto> {

    public ProductoRepository() {
        super(Producto.class);
    }

    /**
     * Busca todos los productos activos que pertenecen a una categoría específica.
     *
     * Consulta JPQL:
     *   - Selecciona Producto (p) cuya categoria tenga el id indicado
     *     y cuyo campo eliminado sea false.
     *   - Usa parámetro nombrado :categoriaId para filtrar por categoría.
     *   - Usa TypedQuery<Producto> para evitar casteos manuales.
     */
    public List<Producto> buscarPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p " +
                    "WHERE p.categoria.id = :categoriaId " +
                    "AND p.eliminado = false";

            TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}