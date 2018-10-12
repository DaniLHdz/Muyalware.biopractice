/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biopractice.controller;

import com.biopractice.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.biopractice.model.Alumno;
import com.biopractice.model.Kit;
import com.biopractice.model.Material;
import com.biopractice.model.Profesor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author alexis
 */
public class KitJpaController implements Serializable {

    public KitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Kit kit) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumno idAlumno = kit.getIdAlumno();
            if (idAlumno != null) {
                idAlumno = em.getReference(idAlumno.getClass(), idAlumno.getIdAlumno());
                kit.setIdAlumno(idAlumno);
            }
            Material idMaterial = kit.getIdMaterial();
            if (idMaterial != null) {
                idMaterial = em.getReference(idMaterial.getClass(), idMaterial.getIdMaterial());
                kit.setIdMaterial(idMaterial);
            }
            Profesor idProfesor = kit.getIdProfesor();
            if (idProfesor != null) {
                idProfesor = em.getReference(idProfesor.getClass(), idProfesor.getIdProfesor());
                kit.setIdProfesor(idProfesor);
            }
            em.persist(kit);
            if (idAlumno != null) {
                idAlumno.getKitCollection().add(kit);
                idAlumno = em.merge(idAlumno);
            }
            if (idMaterial != null) {
                idMaterial.getKitCollection().add(kit);
                idMaterial = em.merge(idMaterial);
            }
            if (idProfesor != null) {
                idProfesor.getKitCollection().add(kit);
                idProfesor = em.merge(idProfesor);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Kit kit) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Kit persistentKit = em.find(Kit.class, kit.getIdKit());
            Alumno idAlumnoOld = persistentKit.getIdAlumno();
            Alumno idAlumnoNew = kit.getIdAlumno();
            Material idMaterialOld = persistentKit.getIdMaterial();
            Material idMaterialNew = kit.getIdMaterial();
            Profesor idProfesorOld = persistentKit.getIdProfesor();
            Profesor idProfesorNew = kit.getIdProfesor();
            if (idAlumnoNew != null) {
                idAlumnoNew = em.getReference(idAlumnoNew.getClass(), idAlumnoNew.getIdAlumno());
                kit.setIdAlumno(idAlumnoNew);
            }
            if (idMaterialNew != null) {
                idMaterialNew = em.getReference(idMaterialNew.getClass(), idMaterialNew.getIdMaterial());
                kit.setIdMaterial(idMaterialNew);
            }
            if (idProfesorNew != null) {
                idProfesorNew = em.getReference(idProfesorNew.getClass(), idProfesorNew.getIdProfesor());
                kit.setIdProfesor(idProfesorNew);
            }
            kit = em.merge(kit);
            if (idAlumnoOld != null && !idAlumnoOld.equals(idAlumnoNew)) {
                idAlumnoOld.getKitCollection().remove(kit);
                idAlumnoOld = em.merge(idAlumnoOld);
            }
            if (idAlumnoNew != null && !idAlumnoNew.equals(idAlumnoOld)) {
                idAlumnoNew.getKitCollection().add(kit);
                idAlumnoNew = em.merge(idAlumnoNew);
            }
            if (idMaterialOld != null && !idMaterialOld.equals(idMaterialNew)) {
                idMaterialOld.getKitCollection().remove(kit);
                idMaterialOld = em.merge(idMaterialOld);
            }
            if (idMaterialNew != null && !idMaterialNew.equals(idMaterialOld)) {
                idMaterialNew.getKitCollection().add(kit);
                idMaterialNew = em.merge(idMaterialNew);
            }
            if (idProfesorOld != null && !idProfesorOld.equals(idProfesorNew)) {
                idProfesorOld.getKitCollection().remove(kit);
                idProfesorOld = em.merge(idProfesorOld);
            }
            if (idProfesorNew != null && !idProfesorNew.equals(idProfesorOld)) {
                idProfesorNew.getKitCollection().add(kit);
                idProfesorNew = em.merge(idProfesorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = kit.getIdKit();
                if (findKit(id) == null) {
                    throw new NonexistentEntityException("The kit with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Kit kit;
            try {
                kit = em.getReference(Kit.class, id);
                kit.getIdKit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The kit with id " + id + " no longer exists.", enfe);
            }
            Alumno idAlumno = kit.getIdAlumno();
            if (idAlumno != null) {
                idAlumno.getKitCollection().remove(kit);
                idAlumno = em.merge(idAlumno);
            }
            Material idMaterial = kit.getIdMaterial();
            if (idMaterial != null) {
                idMaterial.getKitCollection().remove(kit);
                idMaterial = em.merge(idMaterial);
            }
            Profesor idProfesor = kit.getIdProfesor();
            if (idProfesor != null) {
                idProfesor.getKitCollection().remove(kit);
                idProfesor = em.merge(idProfesor);
            }
            em.remove(kit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Kit> findKitEntities() {
        return findKitEntities(true, -1, -1);
    }

    public List<Kit> findKitEntities(int maxResults, int firstResult) {
        return findKitEntities(false, maxResults, firstResult);
    }

    private List<Kit> findKitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Kit.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Kit findKit(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Kit.class, id);
        } finally {
            em.close();
        }
    }

    public int getKitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Kit> rt = cq.from(Kit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
