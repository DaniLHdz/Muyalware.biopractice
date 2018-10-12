/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biopractice.controller;

import com.biopractice.controller.exceptions.IllegalOrphanException;
import com.biopractice.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.biopractice.model.Material;
import java.util.ArrayList;
import java.util.Collection;
import com.biopractice.model.Kit;
import com.biopractice.model.Profesor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author alexis
 */
public class ProfesorJpaController implements Serializable {

    public ProfesorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profesor profesor) {
        if (profesor.getMaterialCollection() == null) {
            profesor.setMaterialCollection(new ArrayList<Material>());
        }
        if (profesor.getKitCollection() == null) {
            profesor.setKitCollection(new ArrayList<Kit>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Material> attachedMaterialCollection = new ArrayList<Material>();
            for (Material materialCollectionMaterialToAttach : profesor.getMaterialCollection()) {
                materialCollectionMaterialToAttach = em.getReference(materialCollectionMaterialToAttach.getClass(), materialCollectionMaterialToAttach.getIdMaterial());
                attachedMaterialCollection.add(materialCollectionMaterialToAttach);
            }
            profesor.setMaterialCollection(attachedMaterialCollection);
            Collection<Kit> attachedKitCollection = new ArrayList<Kit>();
            for (Kit kitCollectionKitToAttach : profesor.getKitCollection()) {
                kitCollectionKitToAttach = em.getReference(kitCollectionKitToAttach.getClass(), kitCollectionKitToAttach.getIdKit());
                attachedKitCollection.add(kitCollectionKitToAttach);
            }
            profesor.setKitCollection(attachedKitCollection);
            em.persist(profesor);
            for (Material materialCollectionMaterial : profesor.getMaterialCollection()) {
                Profesor oldIdProfesorOfMaterialCollectionMaterial = materialCollectionMaterial.getIdProfesor();
                materialCollectionMaterial.setIdProfesor(profesor);
                materialCollectionMaterial = em.merge(materialCollectionMaterial);
                if (oldIdProfesorOfMaterialCollectionMaterial != null) {
                    oldIdProfesorOfMaterialCollectionMaterial.getMaterialCollection().remove(materialCollectionMaterial);
                    oldIdProfesorOfMaterialCollectionMaterial = em.merge(oldIdProfesorOfMaterialCollectionMaterial);
                }
            }
            for (Kit kitCollectionKit : profesor.getKitCollection()) {
                Profesor oldIdProfesorOfKitCollectionKit = kitCollectionKit.getIdProfesor();
                kitCollectionKit.setIdProfesor(profesor);
                kitCollectionKit = em.merge(kitCollectionKit);
                if (oldIdProfesorOfKitCollectionKit != null) {
                    oldIdProfesorOfKitCollectionKit.getKitCollection().remove(kitCollectionKit);
                    oldIdProfesorOfKitCollectionKit = em.merge(oldIdProfesorOfKitCollectionKit);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Profesor profesor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor persistentProfesor = em.find(Profesor.class, profesor.getIdProfesor());
            Collection<Material> materialCollectionOld = persistentProfesor.getMaterialCollection();
            Collection<Material> materialCollectionNew = profesor.getMaterialCollection();
            Collection<Kit> kitCollectionOld = persistentProfesor.getKitCollection();
            Collection<Kit> kitCollectionNew = profesor.getKitCollection();
            List<String> illegalOrphanMessages = null;
            for (Material materialCollectionOldMaterial : materialCollectionOld) {
                if (!materialCollectionNew.contains(materialCollectionOldMaterial)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Material " + materialCollectionOldMaterial + " since its idProfesor field is not nullable.");
                }
            }
            for (Kit kitCollectionOldKit : kitCollectionOld) {
                if (!kitCollectionNew.contains(kitCollectionOldKit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Kit " + kitCollectionOldKit + " since its idProfesor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Material> attachedMaterialCollectionNew = new ArrayList<Material>();
            for (Material materialCollectionNewMaterialToAttach : materialCollectionNew) {
                materialCollectionNewMaterialToAttach = em.getReference(materialCollectionNewMaterialToAttach.getClass(), materialCollectionNewMaterialToAttach.getIdMaterial());
                attachedMaterialCollectionNew.add(materialCollectionNewMaterialToAttach);
            }
            materialCollectionNew = attachedMaterialCollectionNew;
            profesor.setMaterialCollection(materialCollectionNew);
            Collection<Kit> attachedKitCollectionNew = new ArrayList<Kit>();
            for (Kit kitCollectionNewKitToAttach : kitCollectionNew) {
                kitCollectionNewKitToAttach = em.getReference(kitCollectionNewKitToAttach.getClass(), kitCollectionNewKitToAttach.getIdKit());
                attachedKitCollectionNew.add(kitCollectionNewKitToAttach);
            }
            kitCollectionNew = attachedKitCollectionNew;
            profesor.setKitCollection(kitCollectionNew);
            profesor = em.merge(profesor);
            for (Material materialCollectionNewMaterial : materialCollectionNew) {
                if (!materialCollectionOld.contains(materialCollectionNewMaterial)) {
                    Profesor oldIdProfesorOfMaterialCollectionNewMaterial = materialCollectionNewMaterial.getIdProfesor();
                    materialCollectionNewMaterial.setIdProfesor(profesor);
                    materialCollectionNewMaterial = em.merge(materialCollectionNewMaterial);
                    if (oldIdProfesorOfMaterialCollectionNewMaterial != null && !oldIdProfesorOfMaterialCollectionNewMaterial.equals(profesor)) {
                        oldIdProfesorOfMaterialCollectionNewMaterial.getMaterialCollection().remove(materialCollectionNewMaterial);
                        oldIdProfesorOfMaterialCollectionNewMaterial = em.merge(oldIdProfesorOfMaterialCollectionNewMaterial);
                    }
                }
            }
            for (Kit kitCollectionNewKit : kitCollectionNew) {
                if (!kitCollectionOld.contains(kitCollectionNewKit)) {
                    Profesor oldIdProfesorOfKitCollectionNewKit = kitCollectionNewKit.getIdProfesor();
                    kitCollectionNewKit.setIdProfesor(profesor);
                    kitCollectionNewKit = em.merge(kitCollectionNewKit);
                    if (oldIdProfesorOfKitCollectionNewKit != null && !oldIdProfesorOfKitCollectionNewKit.equals(profesor)) {
                        oldIdProfesorOfKitCollectionNewKit.getKitCollection().remove(kitCollectionNewKit);
                        oldIdProfesorOfKitCollectionNewKit = em.merge(oldIdProfesorOfKitCollectionNewKit);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = profesor.getIdProfesor();
                if (findProfesor(id) == null) {
                    throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.");
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
            Profesor profesor;
            try {
                profesor = em.getReference(Profesor.class, id);
                profesor.getIdProfesor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Material> materialCollectionOrphanCheck = profesor.getMaterialCollection();
            for (Material materialCollectionOrphanCheckMaterial : materialCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Profesor (" + profesor + ") cannot be destroyed since the Material " + materialCollectionOrphanCheckMaterial + " in its materialCollection field has a non-nullable idProfesor field.");
            }
            Collection<Kit> kitCollectionOrphanCheck = profesor.getKitCollection();
            for (Kit kitCollectionOrphanCheckKit : kitCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Profesor (" + profesor + ") cannot be destroyed since the Kit " + kitCollectionOrphanCheckKit + " in its kitCollection field has a non-nullable idProfesor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(profesor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Profesor> findProfesorEntities() {
        return findProfesorEntities(true, -1, -1);
    }

    public List<Profesor> findProfesorEntities(int maxResults, int firstResult) {
        return findProfesorEntities(false, maxResults, firstResult);
    }

    private List<Profesor> findProfesorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profesor.class));
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

    public Profesor findProfesor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profesor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfesorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profesor> rt = cq.from(Profesor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
