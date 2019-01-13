package com.ice.model.VO;

import com.ice.model.Function;
import lombok.Data;

import java.util.List;

@Data
public class RoleVO {

    private String id;

    private String name;

    private List<Function> functions;

}
