package com.biopractice.model;

import com.biopractice.model.Material;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-11T22:53:43")
@StaticMetamodel(Administrador.class)
public class Administrador_ { 

    public static volatile SingularAttribute<Administrador, Integer> idAdministrador;
    public static volatile CollectionAttribute<Administrador, Material> materialCollection;
    public static volatile SingularAttribute<Administrador, String> correo;
    public static volatile SingularAttribute<Administrador, String> contrasena;
    public static volatile SingularAttribute<Administrador, String> nombre;

}