package com.ice.model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="library_resourcetype")
public class ResourceType {

    @Id
    private String id;

    private String name;

    // 上一级类型的id，如果是最上级则用-1表示
    private String superiorid;

    //类型所在层级数
    private int level;

}
