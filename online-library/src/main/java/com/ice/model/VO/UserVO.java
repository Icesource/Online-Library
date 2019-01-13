package com.ice.model.VO;

import com.ice.model.Function;
import com.ice.model.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserVO {

    private String id;

    private String username;

    private String password;

    private List<Role> roles;

    private List<Function> functions;

}
