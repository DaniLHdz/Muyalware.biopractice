/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biopractice.controller;

import com.biopractice.controller.exceptions.IllegalOrphanException;
import com.biopractice.controller.exceptions.NonexistentEntityException;
import com.biopractice.model.Alumno;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.biopractice.model.Kit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author alexis
 */
public class AlumnoJpaController implements Serializable {

    public AlumnoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alumno alumno) {
        if (alumno.getKitCollection() == null) {
            alumno.setKitCollection(new ArrayList<Kit>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Kit> attachedKitCollection = new ArrayList<Kit>();
            for (Kit kitCollectionKitToAttach : alumno.getKitCollection()) {
                kitCollectionKitToAttach = em.getReference(kitCollectionKitToAttach.getClass(), kitCollectionKitToAttach.getIdKit());
                attachedKitCollection.add(kitCollectionKitToAttach);
            }
            alumno.setKitCollection(attachedKitCollection);
            em.persist(alumno);
            for (Kit kitCollectionKit : alumno.getKitCollection()) {
                Alumno oldIdAlumnoOfKitCollectionKit = kitCollectionKit.getIdAlumno();
                kitCollectionKit.setIdAlumno(alumno);
                kitCollectionKit = em.merge(kitCollectionKit);
                if (oldIdAlumnoOfKitCollectionKit != null) {
                    oldIdAlumnoOfKitCollectionKit.getKitCollection().remove(kitCollectionKit);
                    oldIdAlumnoOfKitCollectionKit = em.merge(oldIdAlumnoOfKitCollectionKit);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Alumno alumno) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumno persistentAlumno = em.find(Alumno.class, alumno.getIdAlumno());
            Collection<Kit> kitCollectionOld = persistentAlumno.getKitCollection();
            Collection<Kit> kitCollectionNew = alumno.getKitCollection();
            List<String> illegalOrphanMessages = null;
            for (Kit kitCollectionOldKit : kitCollectionOld) {
                if (!kitCollectionNew.contains(kitCollectionOldKit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Kit " + kitCollectionOldKit + " since its idAlumno field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Kit> attachedKitCollectionNew = new ArrayList<Kit>();
            for (Kit kitCollectionNewKitToAttach : kitCollectionNew) {
                kitCollectionNewKitToAttach = em.getReference(kitCollectionNewKitToAttach.getClass(), kitCollectionNewKitToAttach.getIdKit());
                attachedKitCollectionNew.add(kitCollectionNewKitToAttach);
            }
            kitCollectionNew = attachedKitCollectionNew;
            alumno.setKitCollection(kitCollectionNew);
            alumno = em.merge(alumno);
            for (Kit kitCollectionNewKit : kitCollectionNew) {
                if (!kitCollectionOld.contains(kitCollectionNewKit)) {
                    Alumno oldIdAlumnoOfKitCollectionNewKit = kitCollectionNewKit.getIdAlumno();
                    kitCollectionNewKit.setIdAlumno(alumno);
                    kitCollectionNewKit = em.merge(kitCollectionNewKit);
                    if (oldIdAlumnoOfKitCollectionNewKit != null && !oldIdAlumnoOfKitCollectionNewKit.equals(alumno)) {
                        oldIdAlumnoOfKitCollectionNewKit.getKitCollection().remove(kitCollectionNewKit);
                        oldIdAlumnoOfKitCollectionNewKit = em.merge(oldIdAlumnoOfKitCollectionNewKit);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = alumno.getIdAlumno();
                if (findAlumno(id) == null) {
                    throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumno alumno;
            try {
                alumno = em.getReference(Alumno.class, id);
                alumno.getIdAlumno();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Kit> kitCollectionOrphanCheck = alumno.getKitCollection();
            for (Kit kitCollectionOrphanCheckKit : kitCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Alumno (" + alumno + ") cannot be destroyed since the Kit " + kitCollectionOrphanCheckKit + " in its kitCollection field has a non-nullable idAlumno field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(alumno);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Alumno> findAlumnoEntities() {
        return findAlumnoEntities(true, -1, -1);
    }

    public List<Alumno> findAlumnoEntities(int maxResults, int firstResult) {
        return findAlumnoEntities(false, maxResults, firstResult);
    }

    private List<Alumno> findAlumnoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alumno.class));
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

    public Alumno findAlumno(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alumno.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlumnoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alumno> rt = cq.from(Alumno.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
