package com.ice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@IdClass(RoleFunction.class)
@Table(name = "library_role_ref_function")
public class RoleFunction implements Serializable {

    @Id
    private String roleId;

    @Id
    private String functionId;

}
