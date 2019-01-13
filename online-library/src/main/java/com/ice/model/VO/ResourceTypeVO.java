package com.ice.model.VO;

import lombok.Data;

import java.util.List;

@Data
public class ResourceTypeVO {

    private String id;

    private String name;

    private List<ResourceTypeVO> subtypes;

}
