package com.ice.common.SecurityConf;

import com.alibaba.fastjson.JSON;
import com.ice.common.Constant;
import com.ice.common.TMsg;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        TMsg responseBody = new TMsg<>(Constant.SUCCESS, "登陆成功", authentication);
        System.out.println(authentication.getPrincipal());
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(responseBody));
    }
}
