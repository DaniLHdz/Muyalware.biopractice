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
import com.biopractice.model.Administrador;
import com.biopractice.model.Profesor;
import com.biopractice.model.Kit;
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
public class MaterialJpaController implements Serializable {

    public MaterialJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Material material) {
        if (material.getKitCollection() == null) {
            material.setKitCollection(new ArrayList<Kit>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Administrador idAdministrador = material.getIdAdministrador();
            if (idAdministrador != null) {
                idAdministrador = em.getReference(idAdministrador.getClass(), idAdministrador.getIdAdministrador());
                material.setIdAdministrador(idAdministrador);
            }
            Profesor idProfesor = material.getIdProfesor();
            if (idProfesor != null) {
                idProfesor = em.getReference(idProfesor.getClass(), idProfesor.getIdProfesor());
                material.setIdProfesor(idProfesor);
            }
            Collection<Kit> attachedKitCollection = new ArrayList<Kit>();
            for (Kit kitCollectionKitToAttach : material.getKitCollection()) {
                kitCollectionKitToAttach = em.getReference(kitCollectionKitToAttach.getClass(), kitCollectionKitToAttach.getIdKit());
                attachedKitCollection.add(kitCollectionKitToAttach);
            }
            material.setKitCollection(attachedKitCollection);
            em.persist(material);
            if (idAdministrador != null) {
                idAdministrador.getMaterialCollection().add(material);
                idAdministrador = em.merge(idAdministrador);
            }
            if (idProfesor != null) {
                idProfesor.getMaterialCollection().add(material);
                idProfesor = em.merge(idProfesor);
            }
            for (Kit kitCollectionKit : material.getKitCollection()) {
                Material oldIdMaterialOfKitCollectionKit = kitCollectionKit.getIdMaterial();
                kitCollectionKit.setIdMaterial(material);
                kitCollectionKit = em.merge(kitCollectionKit);
                if (oldIdMaterialOfKitCollectionKit != null) {
                    oldIdMaterialOfKitCollectionKit.getKitCollection().remove(kitCollectionKit);
                    oldIdMaterialOfKitCollectionKit = em.merge(oldIdMaterialOfKitCollectionKit);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Material material) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Material persistentMaterial = em.find(Material.class, material.getIdMaterial());
            Administrador idAdministradorOld = persistentMaterial.getIdAdministrador();
            Administrador idAdministradorNew = material.getIdAdministrador();
            Profesor idProfesorOld = persistentMaterial.getIdProfesor();
            Profesor idProfesorNew = material.getIdProfesor();
            Collection<Kit> kitCollectionOld = persistentMaterial.getKitCollection();
            Collection<Kit> kitCollectionNew = material.getKitCollection();
            List<String> illegalOrphanMessages = null;
            for (Kit kitCollectionOldKit : kitCollectionOld) {
                if (!kitCollectionNew.contains(kitCollectionOldKit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Kit " + kitCollectionOldKit + " since its idMaterial field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idAdministradorNew != null) {
                idAdministradorNew = em.getReference(idAdministradorNew.getClass(), idAdministradorNew.getIdAdministrador());
                material.setIdAdministrador(idAdministradorNew);
            }
            if (idProfesorNew != null) {
                idProfesorNew = em.getReference(idProfesorNew.getClass(), idProfesorNew.getIdProfesor());
                material.setIdProfesor(idProfesorNew);
            }
            Collection<Kit> attachedKitCollectionNew = new ArrayList<Kit>();
            for (Kit kitCollectionNewKitToAttach : kitCollectionNew) {
                kitCollectionNewKitToAttach = em.getReference(kitCollectionNewKitToAttach.getClass(), kitCollectionNewKitToAttach.getIdKit());
                attachedKitCollectionNew.add(kitCollectionNewKitToAttach);
            }
            kitCollectionNew = attachedKitCollectionNew;
            material.setKitCollection(kitCollectionNew);
            material = em.merge(material);
            if (idAdministradorOld != null && !idAdministradorOld.equals(idAdministradorNew)) {
                idAdministradorOld.getMaterialCollection().remove(material);
                idAdministradorOld = em.merge(idAdministradorOld);
            }
            if (idAdministradorNew != null && !idAdministradorNew.equals(idAdministradorOld)) {
                idAdministradorNew.getMaterialCollection().add(material);
                idAdministradorNew = em.merge(idAdministradorNew);
            }
            if (idProfesorOld != null && !idProfesorOld.equals(idProfesorNew)) {
                idProfesorOld.getMaterialCollection().remove(material);
                idProfesorOld = em.merge(idProfesorOld);
            }
            if (idProfesorNew != null && !idProfesorNew.equals(idProfesorOld)) {
                idProfesorNew.getMaterialCollection().add(material);
                idProfesorNew = em.merge(idProfesorNew);
            }
            for (Kit kitCollectionNewKit : kitCollectionNew) {
                if (!kitCollectionOld.contains(kitCollectionNewKit)) {
                    Material oldIdMaterialOfKitCollectionNewKit = kitCollectionNewKit.getIdMaterial();
                    kitCollectionNewKit.setIdMaterial(material);
                    kitCollectionNewKit = em.merge(kitCollectionNewKit);
                    if (oldIdMaterialOfKitCollectionNewKit != null && !oldIdMaterialOfKitCollectionNewKit.equals(material)) {
                        oldIdMaterialOfKitCollectionNewKit.getKitCollection().remove(kitCollectionNewKit);
                        oldIdMaterialOfKitCollectionNewKit = em.merge(oldIdMaterialOfKitCollectionNewKit);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = material.getIdMaterial();
                if (findMaterial(id) == null) {
                    throw new NonexistentEntityException("The material with id " + id + " no longer exists.");
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
            Material material;
            try {
                material = em.getReference(Material.class, id);
                material.getIdMaterial();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The material with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Kit> kitCollectionOrphanCheck = material.getKitCollection();
            for (Kit kitCollectionOrphanCheckKit : kitCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Material (" + material + ") cannot be destroyed since the Kit " + kitCollectionOrphanCheckKit + " in its kitCollection field has a non-nullable idMaterial field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Administrador idAdministrador = material.getIdAdministrador();
            if (idAdministrador != null) {
                idAdministrador.getMaterialCollection().remove(material);
                idAdministrador = em.merge(idAdministrador);
            }
            Profesor idProfesor = material.getIdProfesor();
            if (idProfesor != null) {
                idProfesor.getMaterialCollection().remove(material);
                idProfesor = em.merge(idProfesor);
            }
            em.remove(material);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Material> findMaterialEntities() {
        return findMaterialEntities(true, -1, -1);
    }

    public List<Material> findMaterialEntities(int maxResults, int firstResult) {
        return findMaterialEntities(false, maxResults, firstResult);
    }

    private List<Material> findMaterialEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Material.class));
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

    public Material findMaterial(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Material.class, id);
        } finally {
            em.close();
        }
    }

    public int getMaterialCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Material> rt = cq.from(Material.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
