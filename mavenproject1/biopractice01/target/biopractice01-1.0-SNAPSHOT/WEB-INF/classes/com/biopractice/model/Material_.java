package com.biopractice.model;

import com.biopractice.model.Administrador;
import com.biopractice.model.Kit;
import com.biopractice.model.Profesor;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-11T22:53:43")
@StaticMetamodel(Material.class)
public class Material_ { 

    public static volatile SingularAttribute<Material, String> descripcion;
    public static volatile SingularAttribute<Material, Administrador> idAdministrador;
    public static volatile SingularAttribute<Material, Profesor> idProfesor;
    public static volatile SingularAttribute<Material, String> subcategoria;
    public static volatile SingularAttribute<Material, Boolean> prestado;
    public static volatile SingularAttribute<Material, String> categoria;
    public static volatile SingularAttribute<Material, Integer> idMaterial;
    public static volatile SingularAttribute<Material, String> nombre;
    public static volatile CollectionAttribute<Material, Kit> kitCollection;
    public static volatile SingularAttribute<Material, byte[]> fotografia;

}