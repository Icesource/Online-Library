package com.ice.common.SecurityConf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.message.config.AuthConfigProvider;

@Component
public class CusAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = (String) authentication.getPrincipal(); // 这个获取表单输入中返回的用户名;
        String password = (String) authentication.getCredentials(); // 这个是表单中输入的密码；

        String encodePwd = passwordEncoder.encode(password);

        UserDetails userInfo = userDetailService.loadUserByUsername(userName);

        if (!passwordEncoder.matches(password,encodePwd)) {
            throw new BadCredentialsException("用户名密码不正确，请重新登陆！");
        }

        return new UsernamePasswordAuthenticationToken(userInfo, password, userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
