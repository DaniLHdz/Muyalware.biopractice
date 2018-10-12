package com.biopractice.model;

import com.biopractice.model.Kit;
import com.biopractice.model.Material;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-11T22:53:43")
@StaticMetamodel(Profesor.class)
public class Profesor_ { 

    public static volatile SingularAttribute<Profesor, Integer> idAlumno;
    public static volatile CollectionAttribute<Profesor, Material> materialCollection;
    public static volatile SingularAttribute<Profesor, String> numCuenta;
    public static volatile SingularAttribute<Profesor, Integer> idProfesor;
    public static volatile SingularAttribute<Profesor, String> correo;
    public static volatile SingularAttribute<Profesor, Boolean> bloqueado;
    public static volatile SingularAttribute<Profesor, String> contrasena;
    public static volatile SingularAttribute<Profesor, String> numTrabajador;
    public static volatile SingularAttribute<Profesor, String> nombre;
    public static volatile CollectionAttribute<Profesor, Kit> kitCollection;
    public static volatile SingularAttribute<Profesor, byte[]> fotografia;

}