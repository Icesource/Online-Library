package com.ice.model;

import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="library_resource")
public class Resource {

    @Id
    private String id;

    private String name;

    private String format;

    private String link;

    private int clickCount;

    private String uploaderid;

    private String uploaderName;

    private Date createTime;

    private Date updateTime;

    private String typeid;

    private String typeName;

}
