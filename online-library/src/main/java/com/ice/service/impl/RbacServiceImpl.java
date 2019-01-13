package com.ice.service.impl;

import com.ice.service.RbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Component("rbacService")
@Slf4j
public class RbacServiceImpl implements RbacService {

    @Value("${permission.baseUrl}")
    private String baseUrl;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        boolean hasPermission = false;
        if(principal instanceof UserDetails){
            UserDetails userDetails = (UserDetails) principal;
            for(GrantedAuthority grantedAuthority :userDetails.getAuthorities()){
                if(antPathMatcher.match(grantedAuthority.getAuthority(), request.getRequestURI())){
                    log.info("{} 访问 {} 权限有效", grantedAuthority.getAuthority(), request.getRequestURI());
                    hasPermission = true;
                    break;
                }else{
                    log.error("{} 访问 {} 权限无效", grantedAuthority.getAuthority(), request.getRequestURI());
                }
            }
        }
        return hasPermission;
    }

}
