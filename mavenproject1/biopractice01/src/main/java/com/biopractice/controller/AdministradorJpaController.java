/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biopractice.controller;

import com.biopractice.controller.exceptions.IllegalOrphanException;
import com.biopractice.controller.exceptions.NonexistentEntityException;
import com.biopractice.model.Administrador;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.biopractice.model.Material;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author alexis
 */
public class AdministradorJpaController implements Serializable {

    public AdministradorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Administrador administrador) {
        if (administrador.getMaterialCollection() == null) {
            administrador.setMaterialCollection(new ArrayList<Material>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Material> attachedMaterialCollection = new ArrayList<Material>();
            for (Material materialCollectionMaterialToAttach : administrador.getMaterialCollection()) {
                materialCollectionMaterialToAttach = em.getReference(materialCollectionMaterialToAttach.getClass(), materialCollectionMaterialToAttach.getIdMaterial());
                attachedMaterialCollection.add(materialCollectionMaterialToAttach);
            }
            administrador.setMaterialCollection(attachedMaterialCollection);
            em.persist(administrador);
            for (Material materialCollectionMaterial : administrador.getMaterialCollection()) {
                Administrador oldIdAdministradorOfMaterialCollectionMaterial = materialCollectionMaterial.getIdAdministrador();
                materialCollectionMaterial.setIdAdministrador(administrador);
                materialCollectionMaterial = em.merge(materialCollectionMaterial);
                if (oldIdAdministradorOfMaterialCollectionMaterial != null) {
                    oldIdAdministradorOfMaterialCollectionMaterial.getMaterialCollection().remove(materialCollectionMaterial);
                    oldIdAdministradorOfMaterialCollectionMaterial = em.merge(oldIdAdministradorOfMaterialCollectionMaterial);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Administrador administrador) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Administrador persistentAdministrador = em.find(Administrador.class, administrador.getIdAdministrador());
            Collection<Material> materialCollectionOld = persistentAdministrador.getMaterialCollection();
            Collection<Material> materialCollectionNew = administrador.getMaterialCollection();
            List<String> illegalOrphanMessages = null;
            for (Material materialCollectionOldMaterial : materialCollectionOld) {
                if (!materialCollectionNew.contains(materialCollectionOldMaterial)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Material " + materialCollectionOldMaterial + " since its idAdministrador field is not nullable.");
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
            administrador.setMaterialCollection(materialCollectionNew);
            administrador = em.merge(administrador);
            for (Material materialCollectionNewMaterial : materialCollectionNew) {
                if (!materialCollectionOld.contains(materialCollectionNewMaterial)) {
                    Administrador oldIdAdministradorOfMaterialCollectionNewMaterial = materialCollectionNewMaterial.getIdAdministrador();
                    materialCollectionNewMaterial.setIdAdministrador(administrador);
                    materialCollectionNewMaterial = em.merge(materialCollectionNewMaterial);
                    if (oldIdAdministradorOfMaterialCollectionNewMaterial != null && !oldIdAdministradorOfMaterialCollectionNewMaterial.equals(administrador)) {
                        oldIdAdministradorOfMaterialCollectionNewMaterial.getMaterialCollection().remove(materialCollectionNewMaterial);
                        oldIdAdministradorOfMaterialCollectionNewMaterial = em.merge(oldIdAdministradorOfMaterialCollectionNewMaterial);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = administrador.getIdAdministrador();
                if (findAdministrador(id) == null) {
                    throw new NonexistentEntityException("The administrador with id " + id + " no longer exists.");
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
            Administrador administrador;
            try {
                administrador = em.getReference(Administrador.class, id);
                administrador.getIdAdministrador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The administrador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Material> materialCollectionOrphanCheck = administrador.getMaterialCollection();
            for (Material materialCollectionOrphanCheckMaterial : materialCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Administrador (" + administrador + ") cannot be destroyed since the Material " + materialCollectionOrphanCheckMaterial + " in its materialCollection field has a non-nullable idAdministrador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(administrador);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Administrador> findAdministradorEntities() {
        return findAdministradorEntities(true, -1, -1);
    }

    public List<Administrador> findAdministradorEntities(int maxResults, int firstResult) {
        return findAdministradorEntities(false, maxResults, firstResult);
    }

    private List<Administrador> findAdministradorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrador.class));
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

    public Administrador findAdministrador(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Administrador.class, id);
        } finally {
            em.close();
        }
    }

    public int getAdministradorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Administrador> rt = cq.from(Administrador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
