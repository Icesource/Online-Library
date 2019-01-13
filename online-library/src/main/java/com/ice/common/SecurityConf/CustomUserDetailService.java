package com.ice.common.SecurityConf;

import com.ice.model.Function;
import com.ice.model.VO.UserVO;
import com.ice.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 自定义用户认证服务，将登陆的用户（封装好所有可以使用的接口）注入系统
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserVO userVO = userService.findByUserName(s);
        List<Function> functions = userVO.getFunctions();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Function function:functions) {
            authorities.add(new SimpleGrantedAuthority(function.getUrl()));
        }
        User user =new User(userVO.getUsername(), userVO.getPassword(),true,true,
                true,true, authorities);
        return user;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
