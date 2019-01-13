package com.ice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "library_user_ref_role")
@IdClass(UserRole.class)
public class UserRole implements Serializable {

    @Id
    private String userId;

    @Id
    private String roleId;

}
