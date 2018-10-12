package com.biopractice.model;

import com.biopractice.model.Kit;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-11T22:53:43")
@StaticMetamodel(Alumno.class)
public class Alumno_ { 

    public static volatile SingularAttribute<Alumno, Integer> idAlumno;
    public static volatile SingularAttribute<Alumno, String> numCuenta;
    public static volatile SingularAttribute<Alumno, String> correo;
    public static volatile SingularAttribute<Alumno, Boolean> bloqueado;
    public static volatile SingularAttribute<Alumno, String> contrasena;
    public static volatile SingularAttribute<Alumno, String> nombre;
    public static volatile CollectionAttribute<Alumno, Kit> kitCollection;
    public static volatile SingularAttribute<Alumno, byte[]> fotografia;

}