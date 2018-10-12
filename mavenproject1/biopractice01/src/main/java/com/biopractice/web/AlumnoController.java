/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biopractice.web;

import com.biopractice.controller.AlumnoJpaController;
import com.biopractice.model.Alumno;
import com.biopractice.model.PersistenceUtil;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author alexis
 */
@ManagedBean
@RequestScoped
public class AlumnoController {
    
    private Alumno alumno;
    private final AlumnoJpaController ajp;

    /**
     * Creates a new instance of AlumnoController
     */
    public AlumnoController() {
        ajp = new AlumnoJpaController(PersistenceUtil.getEntityManagerFactory());
        alumno = new Alumno();
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno a) {
        alumno = a;
    }

    public String addAlumno() {
        ajp.create(alumno);
        return "lista";
    }

    public List<Alumno> getRegistrados() {
        return ajp.findAlumnoEntities();
    }

}
