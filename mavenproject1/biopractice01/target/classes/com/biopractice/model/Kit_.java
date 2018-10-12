package com.biopractice.model;

import com.biopractice.model.Alumno;
import com.biopractice.model.Material;
import com.biopractice.model.Profesor;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-11T22:53:43")
@StaticMetamodel(Kit.class)
public class Kit_ { 

    public static volatile SingularAttribute<Kit, Alumno> idAlumno;
    public static volatile SingularAttribute<Kit, Profesor> idProfesor;
    public static volatile SingularAttribute<Kit, Date> fechaVencimiento;
    public static volatile SingularAttribute<Kit, Material> idMaterial;
    public static volatile SingularAttribute<Kit, Integer> idKit;

}