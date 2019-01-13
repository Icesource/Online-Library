package com.ice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@Table(name="library_function")
public class Function {

    @Id
    private String id;

    private String name;

    private String url;

}
