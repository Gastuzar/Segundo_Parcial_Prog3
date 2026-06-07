package com.gestion.pedidos.repository;

import com.gestion.pedidos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio genérico abstracto con operaciones CRUD comunes.
 * Las entidades deben tener los campos: id (Long) y eliminado (boolean).
 */
public abstract class BaseRepository<T> {

    private final Class<T> clazz;
    protected final EntityManagerFactory emf;

    public BaseRepository(Class<T> clazz) {
        this.clazz = clazz;
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    // ─── 1. GUARDAR (insert o update) ────────────────────────────────────────
    public T guardar(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = em.merge(entity);
            em.getTransaction().commit();
            return resultado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // ─── 2. BUSCAR POR ID ────────────────────────────────────────────────────
    // Retorna Optional.of(entidad) si existe, Optional.empty() si no
    public Optional<T> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entidad = em.find(clazz, id);
            return Optional.ofNullable(entidad);
        } finally {
            em.close();
        }
    }

    // ─── 3. LISTAR ACTIVOS (eliminado = false) ───────────────────────────────
    // Usa JPQL con el nombre simple de la clase
    public List<T> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName()
                    + " e WHERE e.eliminado = false";
            return em.createQuery(jpql, clazz).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    // ─── 4. BAJA LÓGICA ──────────────────────────────────────────────────────
    // Busca la entidad por ID, setea eliminado = true vía JPQL UPDATE.
    // Retorna true si encontró el registro, false si no existe.
    public boolean eliminarLogico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entidad = em.find(clazz, id);
            if (entidad == null) {
                return false;
            }
            em.getTransaction().begin();
            String jpql = "UPDATE " + clazz.getSimpleName()
                    + " e SET e.eliminado = true WHERE e.id = :id";
            em.createQuery(jpql)
                    .setParameter("id", id)
                    .executeUpdate();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
